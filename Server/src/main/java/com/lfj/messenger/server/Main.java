package com.lfj.messenger.server;

import com.lfj.messenger.codec.JsonCodec;
import com.lfj.messenger.server.dao.ChatDAO;
import com.lfj.messenger.server.dao.ChatMemberDAO;
import com.lfj.messenger.server.dao.MessageDAO;
import com.lfj.messenger.server.dao.UserDAO;
import com.lfj.messenger.server.handlers.ServerHandle;
import com.lfj.messenger.server.registry.ConnectionRegistry;
import com.lfj.messenger.server.service.*;
import com.lfj.messenger.server.threadmanager.ThreadManager;
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

public class Main{
    void main(){
        Logger logger = LoggerFactory.getLogger(Main.class);
        ThreadManager threadManager = new ThreadManager();

        DataSource dataSource = DataBase.createDataSource("jdbc:postgresql://localhost:5432/messfox_db", "postgres", "LolryPI");
        UserDAO userDAO = new UserDAO(dataSource);
        ChatDAO chatDAO = new ChatDAO(dataSource);
        ChatMemberDAO chatMemberDAO = new ChatMemberDAO(dataSource);
        MessageDAO messageDAO = new MessageDAO(dataSource);

        AuthService authService = new AuthService(userDAO, threadManager);
        RegisterService registerService = new RegisterService(userDAO, threadManager);
        MessageSendService messageService = new MessageSendService(messageDAO, threadManager);
        ReadMessageService readMessageService = new ReadMessageService(userDAO, messageDAO, threadManager);
        ChatService chatService = new ChatService(userDAO, chatDAO, chatMemberDAO, threadManager);

        ConnectionRegistry connectionRegistry = new ConnectionRegistry();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
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
                                    new ServerHandle(authService, registerService, messageService, readMessageService, chatService, connectionRegistry)
                            );
                        }
                    });
            ChannelFuture future = bootstrap.bind("localhost", 8081).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            threadManager.shutdown();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}