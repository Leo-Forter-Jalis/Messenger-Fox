package com.lfj.messenger.client;

import com.lfj.messenger.client.events.net.*;
import com.lfj.messenger.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandLoop1 {
    public boolean isInterrupt = false;
    private boolean connected = false;
    private EventBus eventBus;
    void main(){
        this.eventBus = new EventBus();
        this.eventBus.subscribe(ShutdownEvent.class, event -> isInterrupt = true);
        this.eventBus.subscribe(AuthResponseEvent.class, this::authResponse);
        this.eventBus.subscribe(RegisterResponseEvent.class, this::registerResponse);
        this.eventBus.subscribe(MessageResponseEvent.class, this::messageResponse);

        this.eventBus.subscribe(ConnectionEvent.class, event -> connected = true);

        ExecutorService service = Executors.newSingleThreadExecutor();
        Client client = new Client(eventBus);
        service.submit(() -> {
            try {
                client.start();
            }catch (Exception e){
                isInterrupt = true;
                e.printStackTrace();
            }
        });
        int waitConnected = 0;
        while(!connected && waitConnected < 30){
            try {
                TimeUnit.SECONDS.sleep(4);
                waitConnected++;
            } catch (InterruptedException e){
                e.printStackTrace();
                break;
            }
        }
        if(!connected){
            System.err.println("Failed connect to server. Shutting down...");
            eventBus.publish(new ShutdownEvent());
        }else{
            System.out.println("Connected!");
            loop();
        }
        service.shutdown();
        try {
            if (service.awaitTermination(5, TimeUnit.SECONDS)) service.shutdownNow();
        }catch (InterruptedException e){
            service.shutdownNow();
            e.printStackTrace();
        }
        service.close();
        Thread.currentThread().interrupt();
    }
    public void loop(){
        while (!isInterrupt){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String line = br.readLine();
                String[] strings = line.split("\\s+");
                switch (strings[0]) {
                    case "reg" -> {
                        if (strings.length != 5){
                            System.err.printf("The number of arguments in the line %d instead 5\n", strings.length);
                            return;
                        }
                        if (!strings[3].equals(strings[4])){
                            System.err.println("The password doesn't match");
                            return;
                        }
                        this.eventBus.publish(new RegisterRequestEvent(strings[1], strings[2], strings[3]));
                    }
                    case "auth" -> {
                        if (strings.length != 3) {
                            System.err.printf("The number of arguments in the line %d instead 3\n", strings.length);
                            return;
                        }
                        this.eventBus.publish(new AuthRequestEvent(strings[1], strings[2]));
                    }
                    case "message" -> {
                        if(strings.length != 3)
                            System.err.println("The number of arguments in the line %d instead 3\n");
                        this.eventBus.publish(new MessageRequestEvent(strings[1], strings[2]));
                    }
                    case "quit" -> {
                        this.eventBus.publish(new ShutdownEvent());
                    }
                    case "chats" ->{
                        this.eventBus.publish(new ChatsRequestEvent());
                    }
                    default -> System.err.println("Not found command");
                }
            }catch (IOException e){
                e.printStackTrace();
                eventBus.publish(new ShutdownEvent());
            }
        }
    }
    private void authResponse(AuthResponseEvent event){
        System.out.println("Auth success!");
    }
    private void registerResponse(RegisterResponseEvent event){
        System.out.println("Registration success!");
    }
    private void messageResponse(MessageResponseEvent event){
        System.out.printf("%s\nName >> %s\nMessage >> %s\nTime >> %s\n%s\n", "-".repeat(40), event.name(), event.message(), event.time(), "-".repeat(40));
    }
}
