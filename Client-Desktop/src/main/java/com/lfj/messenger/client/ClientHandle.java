package com.lfj.messenger.client;

import com.lfj.dev.annotations.EventBusPublisher;
import com.lfj.messenger.base.db.UserDB;
import com.lfj.messenger.dto.Message;
import com.lfj.messenger.dto.datatype.MessageDTO;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.request.*;
import com.lfj.messenger.dto.response.*;
import com.lfj.messenger.dto.types.MessageType;
import com.lfj.messenger.dto.types.MessageTypeConstants;
import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.events.net.*;
import com.lfj.messenger.time.Time;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class ClientHandle extends ChannelInboundHandlerAdapter {
    private Logger logger;

    private EventBus eventBus;
    private Client client;
    public ClientHandle(EventBus eventBus, Client client){
        this.logger = LoggerFactory.getLogger(ClientHandle.class);
        this.eventBus = eventBus;
        this.client = client;
        this.eventBus.subscribe(AuthRequestEvent.class, this::authRequest);
        this.eventBus.subscribe(RegisterRequestEvent.class, this::registerRequest);
        this.eventBus.subscribe(MessageRequestEvent.class, this::messageRequest);
        this.eventBus.subscribe(ChatsRequestEvent.class, this::chatsRequest);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj){
        if(!(obj instanceof Message message)) return;
        switch (message.type()){
            case MessageTypeConstants.AUTH_RESPONSE -> {
                logger.trace("Authorization success.");
                AuthResponse response = (AuthResponse) message;
                ctx.channel().attr(Attributes.user).set(response.user());
                this.eventBus.publishAsync(new AuthResponseEvent());
            }
            case MessageTypeConstants.REGISTER_RESPONSE -> {
                logger.trace("Register success");
                RegisterResponse response = (RegisterResponse) message;
                ctx.channel().attr(Attributes.user).set(response.user());
                this.eventBus.publishAsync(new RegisterResponseEvent());
            }
            case MessageTypeConstants.MESSAGE_RESPONSE -> {
                logger.trace("Message received");
                MessageResponse response = (MessageResponse) message;
                MessageDTO messageDTO = response.message();
                Channel channel = this.client.getChannel().orElse(null);
                if(channel == null || !channel.isActive()) return;
                List<UserDTO> users = channel.attr(Attributes.users).get();
                if(users == null) return;
                UserDTO sender = users.stream().filter(u -> u.userId().equals(messageDTO.sender())).findFirst().orElse(null);
                if(sender == null) return;
                this.eventBus.publishAsync(new MessageResponseEvent(sender.displayName(), messageDTO.content(), Time.getTime(messageDTO.instant())));
            }
            case MessageTypeConstants.CHATS_RESPONSE -> {
                logger.trace("Chat_response");
                ChatsResponse response = (ChatsResponse) message;
                for(UserDTO user : ((ChatsResponse) message).users()) logger.info("{} - {}\n", user.userId(), user.displayName());
                ctx.channel().attr(Attributes.users).set(response.users());
            }
            case MessageTypeConstants.ERROR_RESPONSE -> {
                ErrorResponse response = (ErrorResponse) message;
                logger.info("{} - {}\n", response.errorMessage(), response.errorCode());
            }
            default -> logger.warn("Invalid message type case.");
        }
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object o){
        if(o instanceof IdleStateEvent event){
            if(event.state() == IdleState.WRITER_IDLE){
                logger.trace("WRITE!");
                ctx.writeAndFlush(new HeartbeatRequest(UUID.randomUUID(), Time.nowInstant()));
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.error("Client-Netty error", cause);
        cause.printStackTrace();
        if(cause instanceof Error) ctx.close();
    }
    private void authRequest(AuthRequestEvent event){
        if(!client.isConnected()){
            logger.warn("Client is not connected. Skip");
            return;
        }
        Channel channel = client.getChannel().orElse(null);
        if(channel != null && channel.isActive()) channel.writeAndFlush(new AuthRequest(UUID.randomUUID(), event.email(), event.password(), Time.nowInstant()));
    }
    private void registerRequest(RegisterRequestEvent event){
        if(!client.isConnected()){
            logger.warn("Client is not connected. Skip");
            return;
        }
        Channel channel = client.getChannel().orElse(null);
        if(channel != null && channel.isActive()) channel.writeAndFlush(new RegisterRequest(UUID.randomUUID(), event.name(), event.name(), event.email(), event.password(), Time.nowInstant()));
    }
    private void messageRequest (MessageRequestEvent event){
        if(!client.isConnected()){
            logger.warn("Client is not connected. Skip");
            return;
        }
        Channel channel = client.getChannel().orElse(null);
        if(channel != null && channel.isActive()) {
            UserDTO user = channel.attr(Attributes.user).get();
            List<UserDTO> users = channel.attr(Attributes.users).get();
            if(user != null && users != null){
                UserDTO receiver = users.stream().filter(u -> u.displayName().equals(event.name())).findFirst().orElse(null);
                if(receiver == null) return;
                MessageDTO message = new MessageDTO(UUID.randomUUID(), null, user, receiver.userId(), MessageType.TEXT.name(), event.message(), Time.nowInstant());
                channel.writeAndFlush(new MessageRequest(UUID.randomUUID(), message, Time.nowInstant()));
            }
        }
    }
    private void chatsRequest(ChatsRequestEvent event){
        if(!client.isConnected()){
            logger.warn("Client is not connected. Skip");
            return;
        }
        Channel channel = client.getChannel().orElse(null);
        if(channel != null && channel.isActive()) channel.writeAndFlush(new ChatsRequest(UUID.randomUUID(), Time.nowInstant()));
    }
    public static class Attributes{
        public static final AttributeKey<UserDTO> user = AttributeKey.valueOf("user");
        public static final AttributeKey<List<UserDTO>> users = AttributeKey.valueOf("users");
    }
}
