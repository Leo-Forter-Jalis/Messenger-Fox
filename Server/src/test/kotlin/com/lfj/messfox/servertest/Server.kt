package com.lfj.messfox.servertest

import com.lfj.messenger.eventbus.EventBus
import com.lfj.messfox.server.Server
import com.lfj.messfox.server.events.ShutdownServerEvent
import com.lfj.messfox.server.events.StartupServerEvent
import org.junit.jupiter.api.Test

class Test {
    @Test
    fun mainTest(){
        val eventBus = EventBus()
        Server(eventBus)
        eventBus.publishAsync(StartupServerEvent(true))
        Thread.sleep(70000)
        eventBus.publish(ShutdownServerEvent())
    }
}