package com.lfj.messfox.server;

import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messfox.server.events.ShowConnectionsEvent;
import com.lfj.messfox.server.events.ShutdownServerEvent;
import com.lfj.messfox.server.events.StartupServerEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    void main() {
        EventBus eventBus = new EventBus();
        new Server(eventBus);
        eventBus.publishAsync(new StartupServerEvent(true));
        loop(eventBus);
        eventBus.clear();
    }
    private void loop(EventBus eventBus){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            cycle: while (true){
                String s = reader.readLine();
                String[] strings = s.split(" ");
                switch (strings[0]){
                    case "exit" -> {
                        eventBus.publish(new ShutdownServerEvent());
                        break cycle;
                    }
                    case "connections" -> {
                        eventBus.publishAsync(new ShowConnectionsEvent());
                    }
                    default -> IO.println("[Error] Unknown command.");
                }
            }
        }catch (IOException e){
            System.err.println("Input-Output error >> " + e.getMessage());
            e.printStackTrace();
        }
    }
}