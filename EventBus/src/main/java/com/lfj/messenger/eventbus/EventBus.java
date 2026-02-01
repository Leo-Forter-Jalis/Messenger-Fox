package com.lfj.messenger.eventbus;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class EventBus {
    private final ConcurrentHashMap<Class<? extends Event>, CopyOnWriteArrayList<SubEvent<?>>> subscribes = new ConcurrentHashMap<>();
    private final AtomicLong aLong = new AtomicLong(0);
    public <E extends Event> void subscribe(Class<E> eventType, Consumer<E> consumer, int priority){
        Objects.requireNonNull(eventType, "EventType cannot be null");
        Objects.requireNonNull(consumer, "Consumer cannot be null");
        validatePriority(priority);

        SubEvent<E> subEvent = new SubEvent<>(consumer, priority, this.aLong.getAndIncrement());
        this.subscribes.compute(eventType, (type, list) -> {
            if(list == null) list = new CopyOnWriteArrayList<>();
            list.add(subEvent);
            list.sort(Comparator
                    .<SubEvent<?>>comparingInt(SubEvent::priority)
                    .thenComparingLong(SubEvent::registrationOlder));
            return list;
        });
    }

    public <E extends Event> void subscribe(Class<E> eventType, Consumer<E> consumer){
        subscribe(eventType, consumer, 0);
    }

    public <E extends Event> void unsubscribe(Class<E> eventType, Consumer<E> consumer){
        CopyOnWriteArrayList<SubEvent<?>> list = this.subscribes.get(eventType);
        list.removeIf(subEvent -> Objects.equals(consumer, subEvent.event()));
    }

    public <E extends Event> void publish(E event){
        Objects.requireNonNull(event, "Event cannot be null");
        CopyOnWriteArrayList<SubEvent<?>> list = this.subscribes.get(event.getClass());
        if(list == null) return;
        for(SubEvent<?> subEvent : list)
            ((SubEvent<E>) subEvent).event().accept(event);
    }

    public <E extends Event> void publishAsync(E event, ExecutorService executorService){
        CompletableFuture.runAsync(() -> publish(event), executorService);
    }

    private void validatePriority(int priority){
        if(priority < -10 || priority > 10) throw new IllegalArgumentException("Priority must be between -10 and 10");
    }
    public void clear(){
        this.subscribes.clear();
    }

    public <E extends Event> void publishAsync(E event) {
        CompletableFuture.runAsync(() -> publish(event));
    }
}
