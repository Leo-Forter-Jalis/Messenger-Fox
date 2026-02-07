package com.lfj.messenger.client;

import com.lfj.messenger.events.net.ConnectionEvent;
import com.lfj.messenger.codec.JsonCodec;
import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.events.net.ShutdownEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Client {
   private Logger logger;

   private ChannelFuture channelFuture;
   private Channel channel;
   private EventLoopGroup loopGroup;
   private EventBus eventBus;
   private volatile boolean connected = false;
   private CountDownLatch downLatch = new CountDownLatch(1);
   public Client(EventBus eventBus){
      this.logger = LoggerFactory.getLogger(Client.class);
      this.eventBus = eventBus;
   }
   public void start(){
      this.loopGroup = new NioEventLoopGroup();
      try {
         Bootstrap bootstrap = new Bootstrap();
         bootstrap.group(loopGroup)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                 .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                       ch.pipeline().addLast(
                               new IdleStateHandler(0, 75, 0),
                               new JsonCodec(),
                               new ClientHandle(eventBus, Client.this)
                       );
                    }
                 });
         this.channelFuture = bootstrap.connect("localhost", 8081);
         channelFuture.addListener((ChannelFutureListener) future ->{
            if(future.isSuccess()){
               this.channel = future.channel();
               connected = true;
               logger.info("Client connected successfully");
               eventBus.publish(new ConnectionEvent());
               channel.closeFuture().addListener(closeFuture ->{
                  logger.info("Connected closed");
                  connected = false;
               });
            }else{
               logger.warn("Failed to connect >> {}\n", future.cause().getMessage());
            }
            this.downLatch.countDown();
         });
         this.eventBus.subscribe(ShutdownEvent.class, this::shutdown);
         if(!this.downLatch.await(10, TimeUnit.SECONDS)){
            logger.info("Connection timeout");
         }
      }catch (Exception e){
         e.printStackTrace();
      }
   }
   private void shutdown(ShutdownEvent event) {
      try {
         this.loopGroup.shutdownGracefully().sync();
         logger.info("Client shutdown complete");
         connected = false;
      }catch (InterruptedException e){
         e.printStackTrace();
      }catch (Exception e){
         e.printStackTrace();
      }
   }
   public Optional<Channel> getChannel(){
      return Optional.ofNullable(channel);
   }
   public boolean isConnected(){
      return connected && channel != null && channel.isActive();
   }
}