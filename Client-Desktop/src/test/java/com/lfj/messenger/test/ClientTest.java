package com.lfj.messenger.test;

import com.lfj.messenger.client.Client;
import com.lfj.messenger.eventbus.EventBus;
import com.lfj.messenger.events.net.AuthRequestEvent;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientTest {
    @Test
    public void meow() throws InterruptedException {
        EventBus eventBus = new EventBus();
        Client client = new Client(eventBus, true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(client::start);
        Thread.sleep(2000);
        eventBus.publish(new AuthRequestEvent("example@mail.com", "12345"));
        executor.awaitTermination(20, TimeUnit.SECONDS);
    }
}
