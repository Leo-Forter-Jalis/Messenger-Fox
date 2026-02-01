package com.lfj.messenger.client.events.net;

import com.lfj.messenger.eventbus.Event;

public record RegisterRequestEvent(
        String email,
        String name,
        String password
) implements Event {
}
