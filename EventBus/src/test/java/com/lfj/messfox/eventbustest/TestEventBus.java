package com.lfj.messfox.eventbustest;

import com.lfj.messfox.eventbus.Event;
import com.lfj.messfox.eventbus.EventBus;
import org.junit.jupiter.api.Test;

public class TestEventBus {
    @Test
    public void test(){
        EventBus eventBus = new EventBus();
        eventBus.subscribe(ImplEvent.class, this::test2);
    }
    private void test2(ImplEvent event){
    }
    class ImplEvent implements Event {}
}
