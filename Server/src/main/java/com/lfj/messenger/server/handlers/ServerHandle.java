package com.lfj.messenger.server.handlers;

import com.lfj.dev.annotations.NeedsRefactoring;
import com.lfj.dev.annotations.ThreadSafe;
import com.lfj.messenger.dto.Message;
import com.lfj.messenger.dto.request.*;
import com.lfj.messenger.dto.response.*;
import com.lfj.messenger.dto.types.MessageTypeConstants;
import com.lfj.messenger.server.registry.ConnectionRegistry;
import com.lfj.messenger.server.service.AuthService;
import com.lfj.messenger.server.service.ChatService;
import com.lfj.messenger.server.service.MessageSendService;
import com.lfj.messenger.server.service.ReadMessageService;
import com.lfj.messenger.server.service.RegisterService;
import com.lfj.messenger.time.Time;

import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@NeedsRefactoring
public class ServerHandle extends ChannelInboundHandlerAdapter {
    private Logger logger;
    private AuthService authService;
    private RegisterService registerService;
    private MessageSendService messageService;
    private ReadMessageService readMessageService;
    private ConnectionRegistry connectionRegistry;
    private ChatService chatService;
    public ServerHandle(AuthService authService, RegisterService registerService, MessageSendService messageService, ReadMessageService readMessageService, ChatService chatService, ConnectionRegistry connectionRegistry){
        this.logger = LoggerFactory.getLogger(ServerHandle.class);
        this.authService = authService;
        this.registerService = registerService;
        this.messageService = messageService;
        this.readMessageService = readMessageService;
        this.connectionRegistry = connectionRegistry;
        this.chatService = chatService;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Connected {}", ctx.channel().remoteAddress());
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
                case MessageTypeConstants.CREATE_CHAT_REQUEST -> {
                    logger.info("{} - Create_Chat_Request type message", ctx.channel().id());
                    handleCreateChatRequest(ctx, (CreateChatRequest) message);
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
    @ThreadSafe
    @NeedsRefactoring
    public void handleAuthRequest(ChannelHandlerContext ctx, AuthRequest request){
        this.authService.authorizationAsync(request)
                .thenAcceptAsync(response ->{
                    ctx.executor().execute(()->{
                        if(response instanceof AuthResponse responseA){ connectionRegistry.add(responseA.user().userId(), ctx.channel()); logger.info("User info id >> {}", responseA.user().userId());}
                        ctx.writeAndFlush(response);
                    });
                });
    }
    @ThreadSafe
    @NeedsRefactoring
    public void handleRegisterRequest(ChannelHandlerContext ctx, RegisterRequest request){
        this.registerService.registrationAsync(request)
                .thenAcceptAsync((response -> {
                    if(response instanceof RegisterResponse responseR){
                        connectionRegistry.add(responseR.user().userId(), ctx.channel());
                    }
                    ctx.executor().execute(() -> ctx.writeAndFlush(response));
                }));
    }

    @ThreadSafe
    @NeedsRefactoring
    public void handleMessageRequest(ChannelHandlerContext ctx, MessageRequest request){
        messageService.sendMessage(request)
                .thenAcceptAsync(response -> {
                    if(response instanceof MessageResponse messageResponse){
                        Set<UUID> userIds = this.chatService.getUsersId(messageResponse.getChatId());
                        if(userIds.isEmpty()){
                            ctx.executor().execute(() -> ctx.writeAndFlush(response));
                            return;
                        }
                        for(UUID userId : userIds){
                            Optional<Channel> channel = this.connectionRegistry.get(userId);
                            channel.ifPresentOrElse(ch ->ctx.executor().execute(() -> ch.writeAndFlush(messageResponse)),
                                    () -> logger.warn("{} is not found for connection registry", userId));
                        }
                    }
                    else ctx.executor().execute(() -> ctx.writeAndFlush(response));
                });
    }
    @ThreadSafe
    public void handleGetMessageRequest(ChannelHandlerContext ctx, GetMessageRequest request){
        this.readMessageService.g(request)
                .thenAcceptAsync(response -> {
                    ctx.executor().execute(() -> {
                        logger.info("{} - sent {}", ctx.channel().remoteAddress(), response.type());
                        ctx.writeAndFlush(response);
                    });
                });
    }
    @ThreadSafe
    public void handleCreateChatRequest(ChannelHandlerContext ctx, CreateChatRequest request){
        this.chatService.createChatAsync(request)
                .thenAcceptAsync(response ->{
                    if(response instanceof CreatedChatResponse response1){
                        logger.info("{}", ctx.channel().remoteAddress());
                        for(UUID id : request.chatData().members()){
                            Optional<Channel> channel = this.connectionRegistry.get(id);
                            channel.ifPresentOrElse(ch -> ctx.executor().execute(() -> ch.writeAndFlush(response1)),
                                    () -> logger.warn("{} is not found for connection registry", id.toString()));
                        }
                    }
                    else if(response instanceof ErrorResponse eResponse){
                        logger.warn("{}: {} - {}", ctx.channel().remoteAddress(), eResponse.errorCode(), eResponse.errorMessage());
                    }
                    ctx.executor().execute(() -> ctx.writeAndFlush(response));
                });
    }
    @ThreadSafe
    public void handleChatsRequest(ChannelHandlerContext ctx, ChatsRequest request){
        this.chatService.getChatAsync(request)
                .thenAcceptAsync(response -> {
                    logger.info("{} - sent {}", ctx.channel().remoteAddress(), response.type());
                    ctx.executor().execute(() -> ctx.writeAndFlush(response));
                });
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("{}", cause);
        ctx.close();
    }
}