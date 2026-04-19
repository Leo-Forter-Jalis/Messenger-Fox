package com.lfj.messenger.client;

import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.events.net.*;
import com.lfj.messenger.time.Time;

import com.lfj.messfox.protocol.Protocol;
import com.lfj.messfox.protocol.request.AuthRequest;
import com.lfj.messfox.protocol.request.HeartbeatRequest;
import com.lfj.messfox.protocol.response.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ClientHandle extends ChannelInboundHandlerAdapter {
    private Logger logger;
    private final String isNotConnection = "Client is not connected. Skip";

    private EventBus eventBus;
    private Client client;
    public ClientHandle(EventBus eventBus, Client client){
        this.logger = LoggerFactory.getLogger(ClientHandle.class);
        this.eventBus = eventBus;
        this.client = client;
        this.eventBus.subscribe(AuthRequestEvent.class, this::authRequest);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj){
        if(!(obj instanceof Protocol)) return;
        switch (obj){
            case AuthResponse response -> {
                eventBus.publish(new AuthResponseEvent(response.getUser()));
            }
            case RegisterResponse response -> {
                eventBus.publish(new RegisterResponseEvent(response.getUser()));
            }
            case ReceiveMessageResponse response -> {
                eventBus.publish(new ReceiveMessageResponseEvent(response.getMessage()));
            }
            case CreatePrivateChatResponse response -> {
                eventBus.publish(new CreatePrivateChatResponseEvent(response.getChat()));
            }
            case CreateGroupChatResponse response -> {
                eventBus.publish(new CreateChatResponseEvent(response.getChat()));
            }
            case FindUserByUsernameResponse response -> {
                eventBus.publish(new FindUserResponseEvent(response.getUsers().getFirst()));
            }
            case GetLastMessageResponse response -> {
                eventBus.publish(new GetLastMessageResponseEvent(response.getLastMessage()));
            }
            case SetUsernameResponse response -> {
                eventBus.publish(new SetUsernameResponseEvent(response.getUser()));
            }
            case ErrorResponse response -> {
                eventBus.publish(new ErrorResponseEvent(response.getException()));
            }
            case HeartbeatsResponse response -> {
                IO.println("Meow");
            }
            default -> IO.println("Meow...");
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
            logger.warn(isNotConnection);
            return;
        }
        Channel channel = client.getChannel().orElse(null);
        if(channel != null && channel.isActive()) channel.writeAndFlush(new AuthRequest(UUID.randomUUID(), event.email(), event.password(), Time.nowInstant()));
    }
    /*
    private void chatsRequest(ChatsRequestEvent event){
        if(!client.isConnected()){
            logger.warn(isNotConnection);
            return;
        }
        Channel channel = client.getChannel().orElse(null);
        if(channel != null && channel.isActive()) channel.writeAndFlush(new ChatsRequest(UUID.randomUUID(), event.chatId(), Time.nowInstant()));
    }
    */
}