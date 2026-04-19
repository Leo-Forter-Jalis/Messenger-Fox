package com.lfj.messenger.eventbus;

import java.util.function.Consumer;

public record SubEvent <E extends Event> (
        Consumer<E> event,
        int priority,
        long registrationOlder
) {  }
