package com.lfj.messfox.server;

import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messfox.server.events.ShowConnectionsEvent;
import com.lfj.messfox.server.events.ShutdownServerEvent;
import com.lfj.messfox.server.events.StartupServerEvent;
import com.lfj.messfox.server.handler.ServerHandler;
import com.lfj.messfox.server.registry.ConnectionRegistry;
import com.lfj.messfox.server.service.DataBase;
import com.lfj.messfox.server.dao.ChatDataTable;
import com.lfj.messfox.server.dao.ChatMemberDataTable;
import com.lfj.messfox.server.dao.MessageDataTable;
import com.lfj.messfox.server.dao.UserDataTable;
import com.lfj.messfox.server.service.ChatService;
import com.lfj.messfox.server.service.MessageService;
import com.lfj.messfox.server.service.UserService;
import com.lfj.messfox.shared.codec.JsonCodec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class Server {
    private final String LOGGER_NAME = "SERVER";
    private Logger logger;
    private ConnectionRegistry connectionRegistry;
    private Server(){  }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public Server(EventBus eventBus){
        this.logger = LoggerFactory.getLogger(LOGGER_NAME);
        eventBus.subscribe(StartupServerEvent.class, this::startup);
        eventBus.subscribe(ShutdownServerEvent.class, this::shutdown);
        eventBus.subscribe(ShowConnectionsEvent.class, this::showConnections);
    }

    private void startup(StartupServerEvent event){
        DataSource dataSource = event.isTest() ? DataBase.H2TestDB.dataSource() : DataBase.PostgresDB.dataSource();

        UserDataTable.H2UserDAO userDAO = new UserDataTable.H2UserDAO(dataSource);
        UserService userService = new UserService(userDAO);
        ChatService chatService = new ChatService(new ChatDataTable.H2ChatDAO(dataSource), new ChatMemberDataTable.H2ChatMemberDao(dataSource));
        MessageService messageService = new MessageService(new MessageDataTable.H2MessageDAO(dataSource), userDAO);

        this.connectionRegistry = new ConnectionRegistry();
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        try{
            logger.info("settings...");
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel){
                            socketChannel.pipeline().addLast(
                                    new IdleStateHandler(80, 0, 0),
                                    new JsonCodec(),
                                    new ServerHandler(userService, chatService, messageService, connectionRegistry)
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind("localhost", 8081).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void shutdown(ShutdownServerEvent event){
        logger.info("Server shutdown...");
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
    private void showConnections(ShowConnectionsEvent event){
        this.connectionRegistry.getConnectionsChannel().forEach(System.out::println);
    }
}