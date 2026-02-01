package com.lfj.messenger.server.handlers;

import com.lfj.messenger.dto.Message;
import com.lfj.messenger.dto.request.AuthRequest;
import com.lfj.messenger.dto.request.ChatsRequest;
import com.lfj.messenger.dto.request.MessageRequest;
import com.lfj.messenger.dto.request.RegisterRequest;
import com.lfj.messenger.dto.request.GetMessageRequest;
import com.lfj.messenger.dto.response.*;
import com.lfj.messenger.dto.types.MessageTypeConstants;
import com.lfj.messenger.server.ConnectionRegistry;
import com.lfj.messenger.server.service.AuthService;
import com.lfj.messenger.server.service.ListUserService;
import com.lfj.messenger.server.service.MessageSendService;
import com.lfj.messenger.server.service.RegisterService;
import com.lfj.messenger.time.Time;
import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.time.Instant;
import java.util.UUID;

public class ServerHandle extends ChannelInboundHandlerAdapter {
    private Logger logger;
    private AuthService authService;
    private RegisterService registerService;
    private MessageSendService messageService;
    private ListUserService listUserService;
    private ConnectionRegistry connectionRegistry;
    public ServerHandle(AuthService authService, RegisterService registerService, MessageSendService messageService, ListUserService listUserService, ConnectionRegistry connectionRegistry){
        this.logger = LoggerFactory.getLogger(ServerHandle.class);
        this.authService = authService;
        this.registerService = registerService;
        this.messageService = messageService;
        this.listUserService = listUserService;
        this.connectionRegistry = connectionRegistry;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Connected {}", ctx.channel().remoteAddress());
        ctx.writeAndFlush(new ErrorResponse(UUID.randomUUID(), (short) 300, "III", Time.nowInstant()));
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object){
        if(object instanceof Message message){
            switch (message.type()){
                case MessageTypeConstants.HEARTBEAT_REQUEST -> {
                    logger.debug("{}. Ip - {} Heartbeat", ctx.channel().id(), ctx.channel().remoteAddress());
                }
                case MessageTypeConstants.AUTH_REQUEST -> {
                    logger.info("{} - Auth_Request type message", ctx.channel().id());
                    handleAuthRequest(ctx, (AuthRequest) message);
                }
                case MessageTypeConstants.MESSAGE_REQUEST -> {
                    logger.info("{} - Message_Request type message", ctx.channel().id());
                    handleMessageRequest(ctx, (MessageRequest) message);
                }
                case MessageTypeConstants.REGISTER_REQUEST -> {
                    logger.info("{} - Register_Request type message", ctx.channel().id());
                    handleRegisterRequest(ctx, (RegisterRequest) message);
                }
                case MessageTypeConstants.CHATS_REQUEST -> {
                    logger.info("{} - Chat_Request type message", ctx.channel().id());
                    handleChatsRequest(ctx, (ChatsRequest) message);
                }
                case MessageTypeConstants.GET_MESSAGE_REQUEST -> {
                    logger.info("{} - Get_Message_Request type message", ctx.channel().id());
                    handleGetMessageRequest(ctx, (GetMessageRequest) message);
                }
                default -> {
                    ErrorResponse response = new ErrorResponse(message.requestId(), (short) 500, "Error: Invalid message_type", Instant.now());
                    logger.error("Handle {} invalid message type!", ctx.channel().id());
                    ctx.writeAndFlush(response);
                }
            }
        }else logger.info("{}", object);
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object object) throws Exception {
        if(object instanceof IdleStateEvent event){ // Вызывается, если долго не поступало сообщений от клиента
            if(event.state() == IdleState.ALL_IDLE){
                logger.warn("Client {} timed out (no heartbeat) Closing channel...)", ctx.channel().remoteAddress());
                ctx.writeAndFlush(new HeartbeatResponse(null, Time.nowInstant()));
                ctx.close();
            }
        }else{
            super.userEventTriggered(ctx, object);
        }
    }
    public void handleAuthRequest(ChannelHandlerContext ctx, AuthRequest request){
        this.authService.authorizationAsync(request)
                .thenAcceptAsync(response ->{
                    ctx.executor().execute(()->{
                        logger.info("Netty EventLoop...");
                        logger.info("{}", response);
                        if(response instanceof AuthResponse responseA){ connectionRegistry.add(responseA.user().userId(), ctx.channel()); logger.info("User info id >> {}", responseA.user().userId());}
                        ctx.writeAndFlush(response);
                    });
                });
    }
    public void handleRegisterRequest(ChannelHandlerContext ctx, RegisterRequest request){
        this.registerService.registrationAsync(request)
                .thenAcceptAsync((response -> {
                    logger.info("{}", response);
                    if(response instanceof RegisterResponse responseR){
                        connectionRegistry.add(responseR.user().userId(), ctx.channel());
                        logger.info("User info id >> {}", responseR.user().userId());
                    }
                    ctx.executor().execute(() -> ctx.writeAndFlush(response));
                }));
    }

    public void handleMessageRequest(ChannelHandlerContext ctx, MessageRequest request){
        messageService.sendMessage(request)
                .thenAcceptAsync(response -> {
                    ctx.executor().execute(()->{
                        s: if(response instanceof MessageResponse responseM){
                            connectionRegistry.display();
                            if(!connectionRegistry.isOnline(responseM.getReceiverId())) logger.info("User {} is not Online", responseM.getReceiverId());
                            Channel channel = connectionRegistry.get(responseM.getReceiverId()).orElse(null);
                            if(channel == null) break s;
                            channel.writeAndFlush(responseM);
                            return;
                        }
                        ctx.writeAndFlush(response);
                    });
                });
    }
    public void handleChatsRequest(ChannelHandlerContext ctx, ChatsRequest request){
        this.listUserService.listUsers(request)
                .thenAcceptAsync(response -> ctx.executor().execute(() -> ctx.writeAndFlush(response)));
    }
    public void handleGetMessageRequest(ChannelHandlerContext ctx, GetMessageRequest request){

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("{}", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
