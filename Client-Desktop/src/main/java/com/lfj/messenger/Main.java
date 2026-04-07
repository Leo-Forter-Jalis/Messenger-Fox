package com.lfj.messenger;

import com.lfj.messenger.client.Client;
import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.events.net.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    private volatile boolean connected = false;
    private EventBus eventBus;
    private ExecutorService service;
    void main() {
        this.eventBus = new EventBus();
        this.eventBus.subscribe(ConnectionEvent.class, event -> connected = true);
        this.eventBus.subscribe(ShutdownEvent.class, event -> this.connected = false);
        this.service = Executors.newFixedThreadPool(3);
        Client client = new Client(eventBus);
        Future<?> clientFuture = this.service.submit(client::start);
        short waitConnected = 0;
        while(!connected && waitConnected < 30){
            try{
                System.out.println(waitConnected);
                TimeUnit.SECONDS.sleep(3);
                waitConnected++;
            }catch (InterruptedException e){
                e.printStackTrace();
                break;
            }
        }
        if(!connected && clientFuture.isDone()){
            this.eventBus.publish(new ShutdownEvent());
            clientFuture.cancel(true);
            this.eventBus.clear();
            close();
        }
        if(connected){
            while (connected){
                try{
                    Thread.sleep(300);
                }catch (InterruptedException e){
                    this.eventBus.publish(new ShutdownEvent());
                    e.printStackTrace();
                }
            }
            if(!connected || !clientFuture.isDone()){
                clientFuture.cancel(true);
                close();
            }
        }
        this.service.close();
    }
    private void close(){
        this.service.shutdown();
        try{
            if(this.service.awaitTermination(5, TimeUnit.SECONDS)) this.service.shutdownNow();
        }catch (InterruptedException e){
            this.service.shutdownNow();
        }
    }
}
