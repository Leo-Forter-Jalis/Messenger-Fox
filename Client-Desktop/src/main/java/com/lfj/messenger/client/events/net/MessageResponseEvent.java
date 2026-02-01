package com.lfj.messenger.client.events.net;

import com.lfj.messenger.eventbus.Event;

public record MessageResponseEvent(
    String name,
    String message,
    String time
) implements Event {
}
