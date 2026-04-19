package com.lfj.messenger.test;

import com.lfj.messenger.client.Client;
import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.events.net.*;
import com.lfj.messfox.protocol.datatype.Chat;
import com.lfj.messfox.protocol.datatype.User;
import com.lfj.messfox.protocol.type.MessageType;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ClientTest {
    public void main() throws InterruptedException {
        EventBus eventBus = new EventBus();
        Client client = new Client(eventBus, true);
        AtomicReference<Chat> chat = new AtomicReference<>(null);
        AtomicReference<Chat> pChat = new AtomicReference<>(null);
        AtomicReference<User> user = new AtomicReference<>(null);
        eventBus.subscribe(CreateChatResponseEvent.class, (chatResponseEvent) ->{
            chat.set(chatResponseEvent.chat());
        });
        eventBus.subscribe(FindUserResponseEvent.class, event ->{
            if(!event.user().getDisplayName().equals("Example")) user.set(event.user());
        });
        eventBus.subscribe(CreatePrivateChatResponseEvent.class, event ->{
            pChat.set(event.chat());
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(client::start);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try{
            cycle: while(true){
                String line = reader.readLine();
                switch (line){
                    case "reg" -> eventBus.publish(new RegisterRequestEvent("example@mail.com", "Example", "12345"));
                    case "auth" -> eventBus.publish(new AuthRequestEvent("example@mail.com", "12345"));
                    case "find" -> eventBus.publish(new FindUserRequestEvent("TEST2"));
                    case "setUsername" -> eventBus.publish(new SetUsernameRequestEvent("TEST1"));
                    case "groupChat" -> eventBus.publish(new CreateChatRequestEvent("Meow"));
                    case "privateChat" -> {
                        if(user.get() != null) eventBus.publish(new CreatePrivateChatRequestEvent(user.get().getUserId()));
                        else IO.println("User is empty");
                    }
                    case "sentInGroupC" -> {
                        if(chat.get() != null) eventBus.publish(new SentMessageRequestEvent(chat.get().getChatId(), MessageType.TEXT, "Hello"));
                    }
                    case "sentInPrivateC" ->{
                        if(pChat.get() != null) eventBus.publish(new SentMessageRequestEvent(pChat.get().getChatId(), MessageType.TEXT, "HELLO!"));
                    }
                    case "getLastMessagePChat" -> {
                        if(pChat.get() != null) eventBus.publish(new GetLastMessageRequestEvent(pChat.get().getChatId()));
                    }
                    case "shutdown" -> {
                        eventBus.publish(new ShutdownEvent());
                        break cycle;
                    }
                    default -> IO.println("Command not found");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
