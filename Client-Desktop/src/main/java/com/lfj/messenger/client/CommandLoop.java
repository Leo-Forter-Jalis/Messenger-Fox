package com.lfj.messenger.client;

import com.lfj.dev.annotations.RequiresDeletion;
import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.events.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RequiresDeletion(version = "0.1", replacement = "com.lfj.messenger.ui.Window")
public class CommandLoop {
    public boolean isInterrupt = false;
    private Logger logger;
    private EventBus eventBus;
    public CommandLoop(EventBus eventBus){
        this.logger = LoggerFactory.getLogger(CommandLoop.class);
        this.eventBus = eventBus;
    }
    public void startLoop(){
        this.eventBus.subscribe(ShutdownEvent.class, event -> isInterrupt = true);
        this.eventBus.subscribe(AuthResponseEvent.class, this::authResponse);
        this.eventBus.subscribe(RegisterResponseEvent.class, this::registerResponse);
        this.eventBus.subscribe(MessageResponseEvent.class, this::messageResponse);
        loop();
    }
    private void loop(){
        while (!isInterrupt){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String line = br.readLine();
                String[] strings = line.split("\\s+");
                switch (strings[0]) {
                    case "reg" -> {
                        if (strings.length != 5){
                            logger.warn("The number of arguments in the line {} instead 5", strings.length);
                            return;
                        }
                        if (!strings[3].equals(strings[4])){
                            logger.warn("The password doesn't match");
                            return;
                        }
                        this.eventBus.publish(new RegisterRequestEvent(strings[1], strings[2], strings[3]));
                    }
                    case "auth" -> {
                        if (strings.length != 3) {
                            logger.warn("The number of arguments in the line {} instead 3", strings.length);
                            return;
                        }
                        this.eventBus.publish(new AuthRequestEvent(strings[1], strings[2]));
                    }
                    case "message" -> {
                        if(strings.length != 3)
                            logger.warn("The number of arguments in the line {} instead 3", strings.length);
                        this.eventBus.publish(new MessageRequestEvent(strings[1], strings[2]));
                    }
                    case "quit" -> {
                        this.eventBus.publish(new ShutdownEvent());
                    }
                    case "chats" ->{
                        this.eventBus.publish(new ChatsRequestEvent());
                    }
                    default -> logger.warn("Not found command");
                }
            }catch (IOException e){
                logger.error("Client command loop error", e);
                e.printStackTrace();
                eventBus.publish(new ShutdownEvent());
            }
        }
    }
    private void authResponse(AuthResponseEvent event){
        logger.info("Auth success!");
    }
    private void registerResponse(RegisterResponseEvent event){
        logger.info("Registration success!");
    }
    private void messageResponse(MessageResponseEvent event){
        logger.info("{}\nName >> {}\nMessage >> {}\nTime >> {}\n{}", "-".repeat(40), event.name(), event.message(), event.time(), "-".repeat(40));
    }
}
