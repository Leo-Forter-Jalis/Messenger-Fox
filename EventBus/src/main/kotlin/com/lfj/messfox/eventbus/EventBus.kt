package com.lfj.messfox.eventbus

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

typealias E = Event

class EventBus{
    private val map: ConcurrentMap<Class<out E>, CopyOnWriteArrayList<SubEventContainer>> = ConcurrentHashMap()
    fun subscribe(eventType: Class<out E>, listener: (event: E) -> Unit){
        SubEventContainer(listener = listener, priority = 2)
    }
    fun subscribe(eventType: Class<out E>, listener: Consumer<out E>){
        SubEventContainer(listener = listener, 1)
    }
}

interface Event
interface KotlinEvent : Event

private data class SubEventContainer(val listener: Consumer<out E>, val priority: Int)
