package com.lfj.messenger.client;

import com.lfj.dev.annotations.EventBusSubscriber;
import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.events.net.*;
import com.lfj.messfox.protocol.request.*;
import com.lfj.messfox.protocol.response.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.time.Instant;
import java.util.UUID;

public class TestClientHandler extends ChannelInboundHandlerAdapter {
    private EventBus eventBus;
    private Client client;
    public TestClientHandler(EventBus eventBus, Client client){
        this.eventBus = eventBus;
        this.client = client;
        this.eventBus.subscribe(AuthRequestEvent.class, this::auth);
        this.eventBus.subscribe(RegisterRequestEvent.class, this::register);
        this.eventBus.subscribe(CreateChatRequestEvent.class, this::chat);
        this.eventBus.subscribe(SentMessageRequestEvent.class, this::message);
        this.eventBus.subscribe(SetUsernameRequestEvent.class, this::setUserName);
        this.eventBus.subscribe(CreatePrivateChatRequestEvent.class, this::privateChat);
        this.eventBus.subscribe(FindUserRequestEvent.class, this::find);
        this.eventBus.subscribe(GetLastMessageRequestEvent.class, this::getLastMessage);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if(msg instanceof AuthResponse response){
            eventBus.publish(new AuthResponseEvent(response.getUser()));
        }else if(msg instanceof CreateGroupChatResponse response){
            eventBus.publish(new CreateChatResponseEvent(response.component2()));
        }else if(msg instanceof FindUserByUsernameResponse response) {
            eventBus.publish(new FindUserResponseEvent(response.getUsers().getFirst()));
        } else if (msg instanceof CreatePrivateChatResponse response) {
            eventBus.publish(new CreatePrivateChatResponseEvent(response.getChat()));
        } else if (msg instanceof ReceiveMessageResponse response) {
            eventBus.publish(new ReceiveMessageResponseEvent(response.getMessage()));
        }
        IO.println("Reading...");
        IO.println(msg.toString());
    }
    @EventBusSubscriber
    private void auth(AuthRequestEvent event){
        client.getChannel().ifPresent(ch -> {
            ch.writeAndFlush(new AuthRequest(UUID.randomUUID(), event.email(), event.password(), Instant.now()));
        });
    }
    @EventBusSubscriber
    private void register(RegisterRequestEvent event){
        client.getChannel().ifPresent(ch -> {
            ch.writeAndFlush(new RegisterRequest(UUID.randomUUID(), event.name(), event.email(), event.password(), Instant.now()));
        });
    }
    @EventBusSubscriber
    private void chat(CreateChatRequestEvent event){
        client.getChannel().ifPresent(ch -> {
            ch.writeAndFlush(new CreateGroupChatRequest(UUID.randomUUID(), event.chatName(), null, null, Instant.now()));
        });
    }
    private void privateChat(CreatePrivateChatRequestEvent event){
        client.getChannel().ifPresent(ch ->{
            ch.writeAndFlush(new CreatePrivateChatRequest(UUID.randomUUID(), event.userId(), Instant.now()));
        });
    }
    @EventBusSubscriber
    private void message(SentMessageRequestEvent event){
        client.getChannel().ifPresent(ch ->{
            ch.writeAndFlush(new SendMessageRequest(UUID.randomUUID(), event.chatId(), event.messageType(), event.content(), Instant.now()));
        });
    }
    @EventBusSubscriber
    private void setUserName(SetUsernameRequestEvent event){
        client.getChannel().ifPresent(ch ->{
            ch.writeAndFlush(new SetUsernameRequest(UUID.randomUUID(), event.userName(), Instant.now()));
        });
    }
    @EventBusSubscriber
    private void find(FindUserRequestEvent event){
        client.getChannel().ifPresent(ch ->{
            ch.writeAndFlush(new FindUserByUsernameRequest(UUID.randomUUID(), event.userName(), Instant.now()));
        });
    }
    @EventBusSubscriber
    private void getLastMessage(GetLastMessageRequestEvent event){
        client.getChannel().ifPresent(ch ->{
            ch.writeAndFlush(new GetLastMessageRequest(UUID.randomUUID(), event.chatId(), Instant.now()));
        });
    }
}
