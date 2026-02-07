package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;

public record MessageRequestEvent(
        String name,
        String message
) implements Event {
}
