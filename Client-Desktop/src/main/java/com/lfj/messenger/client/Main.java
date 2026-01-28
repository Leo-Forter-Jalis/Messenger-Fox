package com.lfj.messenger.client;

import com.lfj.messenger.codec.JsonCodec;
import com.lfj.messenger.dto.datatype.MessageDTO;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.request.AuthRequest;
import com.lfj.messenger.dto.request.ChatsRequest;
import com.lfj.messenger.dto.request.MessageRequest;
import com.lfj.messenger.dto.request.RegisterRequest;
import com.lfj.messenger.dto.types.MessageType;
import com.lfj.messenger.time.Time;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class Main{
   private ChannelFuture future;
   private ClientHandle clientHandle;
   private UserDTO userDTO;
   void main(){
      new Main().start();
   }
   public void start(){
      EventLoopGroup loopGroup = new NioEventLoopGroup();
      try{
         this.clientHandle = new ClientHandle();
         Bootstrap bootstrap = new Bootstrap();
         bootstrap.group(loopGroup)
                 .channel(NioSocketChannel.class)
                 .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch){
                       ch.pipeline().addLast(
                               new IdleStateHandler(0, 75, 0),
                               new JsonCodec(),
                               new ClientHandle()
                       );
                    }
                 });
         this.future = bootstrap.connect("localhost", 8081).sync();
         System.out.println("Client started!");
         commandLoop();
      }catch (InterruptedException e){
         e.printStackTrace();
      } finally{
         loopGroup.shutdownGracefully();
      }
   }
   private void commandLoop() throws InterruptedException {
      try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
         cycle: while (true){
            String line = reader.readLine();
            String[] parts = line.split(" ");
            switch (parts[0]){
               case "auth" -> {
                  if (parts.length < 3) {
                     System.out.println("Error! Length invalid");
                     break;
                  }
                  future.channel().writeAndFlush(
                          new AuthRequest(UUID.randomUUID(), parts[1], parts[2], Time.nowInstant())
                  );
               }
               case "reg" ->{
                  if(parts.length < 5){
                     System.out.println("Error! Length invalid");
                     break;
                  }
                  future.channel().writeAndFlush(
                          new RegisterRequest(UUID.randomUUID(), parts[1], parts[2], parts[3], parts[4], Time.nowInstant())
                  );
               }
               case "message" ->{
                  if(clientHandle.user().isEmpty()) return;
                  if(clientHandle.users().isEmpty()) return;
                  UserDTO userDTO = clientHandle.user().get();
                  UserDTO receiver = clientHandle.users().get().stream().filter(u -> u.displayName().equals(parts[1])).findFirst().orElse(null);
                  if(receiver == null) break;
                  future.channel().writeAndFlush(
                          new MessageRequest(
                                  UUID.randomUUID(),
                                  new MessageDTO(
                                          UUID.randomUUID(),
                                          null,
                                          userDTO.userId(),
                                          receiver.userId(),
                                          MessageType.TEXT.name(),
                                          parts[2],
                                          Time.nowInstant()
                                  ),
                                  Time.nowInstant()
                          )
                     );
               }
               case "chats" ->{
                  future.channel().writeAndFlush(new ChatsRequest(UUID.randomUUID(), Time.nowInstant()));
               }
               case "quit" ->{
                  System.out.println("Shutdown");
                  break cycle;
               }
               default -> System.out.println("Invalid command");
            }
         }
      }catch (IOException e){
         e.printStackTrace();
      }
   }
}