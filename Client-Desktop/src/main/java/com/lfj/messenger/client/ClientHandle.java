package com.lfj.messenger.client;

import com.lfj.messenger.client.events.net.*;
import com.lfj.messenger.dto.Message;
import com.lfj.messenger.dto.datatype.MessageDTO;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.request.*;
import com.lfj.messenger.dto.response.*;
import com.lfj.messenger.dto.types.MessageType;
import com.lfj.messenger.dto.types.MessageTypeConstants;
import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.time.Time;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.UUID;

public class ClientHandle extends ChannelInboundHandlerAdapter {
    private EventBus eventBus;
    private Client client;
    public ClientHandle(EventBus eventBus, Client client){
        this.eventBus = eventBus;
        this.client = client;
        this.eventBus.subscribe(AuthRequestEvent.class, this::authRequest);
        this.eventBus.subscribe(RegisterRequestEvent.class, this::registerRequest);
        this.eventBus.subscribe(MessageRequestEvent.class, this::messageRequest);
        this.eventBus.subscribe(ChatsRequestEvent.class, this::chatsRequest);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws InterruptedException {
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj){
        if(!(obj instanceof Message message)) return;
        switch (message.type()){
            case MessageTypeConstants.AUTH_RESPONSE -> {
                System.out.println("Authorization success.");
                AuthResponse response = (AuthResponse) message;
                ctx.channel().attr(Attributes.user).set(response.user());
                this.eventBus.publish(new AuthResponseEvent());
            }
            case MessageTypeConstants.REGISTER_RESPONSE -> {
                System.out.println("Register success");
                RegisterResponse response = (RegisterResponse) message;
                ctx.channel().attr(Attributes.user).set(response.user());
                this.eventBus.publish(new RegisterResponseEvent());
            }
            case MessageTypeConstants.MESSAGE_RESPONSE -> {
                System.out.println("Message received");
                MessageResponse response = (MessageResponse) message;
                MessageDTO messageDTO = response.message();
                Channel channel = this.client.getChannel().orElse(null);
                if(channel == null || !channel.isActive()) return;
                List<UserDTO> users = channel.attr(Attributes.users).get();
                if(users == null) return;
                UserDTO sender = users.stream().filter(u -> u.userId().equals(messageDTO.senderId())).findFirst().orElse(null);
                if(sender == null) return;
                this.eventBus.publish(new MessageResponseEvent(sender.displayName(), messageDTO.content(), Time.getTime(messageDTO.instant())));
            }
            case MessageTypeConstants.CHATS_RESPONSE -> {
                System.out.println("Chat_response");
                ChatsResponse response = (ChatsResponse) message;
                for(UserDTO user : ((ChatsResponse) message).users()) System.out.printf("%s - %s\n", user.userId(), user.displayName());
                ctx.channel().attr(Attributes.users).set(response.users());
            }
            case MessageTypeConstants.ERROR_RESPONSE -> {
                ErrorResponse response = (ErrorResponse) message;
                System.out.printf("%s - %d\n", response.errorMessage(), response.errorCode());
            }
            default -> System.out.println("Invalid message type case.");
        }
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object o){
        if(o instanceof IdleStateEvent event){
            if(event.state() == IdleState.WRITER_IDLE){
                System.out.println("WRITE!");
                ctx.writeAndFlush(new HeartbeatRequest(UUID.randomUUID(), Time.nowInstant()));
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.err.println("Error >> " + cause.getMessage());
        cause.printStackTrace();
        if(cause instanceof Error) ctx.close();
    }
    private void authRequest(AuthRequestEvent event){
        if(!client.isConnected()){
            System.out.println("NOT");
            return;
        }
        Channel channel = client.getChannel().orElse(null);
        if(channel != null && channel.isActive()) channel.writeAndFlush(new AuthRequest(UUID.randomUUID(), event.email(), event.password(), Time.nowInstant()));
    }
    private void registerRequest(RegisterRequestEvent event){
        if(!client.isConnected()){
            System.out.println("NOT");
            return;
        }
        Channel channel = client.getChannel().orElse(null);
        if(channel != null && channel.isActive()) channel.writeAndFlush(new RegisterRequest(UUID.randomUUID(), event.name(), event.name(), event.email(), event.password(), Time.nowInstant()));
    }
    private void messageRequest (MessageRequestEvent event){
        if(!client.isConnected()){
            System.out.println("NOT");
            return;
        }
        Channel channel = client.getChannel().orElse(null);
        if(channel != null && channel.isActive()) {
            UserDTO user = channel.attr(Attributes.user).get();
            List<UserDTO> users = channel.attr(Attributes.users).get();
            if(user != null && users != null){
                UserDTO receiver = users.stream().filter(u -> u.displayName().equals(event.name())).findFirst().orElse(null);
                if(receiver == null) return;
                MessageDTO message = new MessageDTO(UUID.randomUUID(), null, user.userId(), receiver.userId(), MessageType.TEXT.name(), event.message(), Time.nowInstant());
                channel.writeAndFlush(new MessageRequest(UUID.randomUUID(), message, Time.nowInstant()));
            }
        }
        System.out.println("Проеб");
    }
    private void chatsRequest(ChatsRequestEvent event){
        if(!client.isConnected()){
            System.out.println("NOT");
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
