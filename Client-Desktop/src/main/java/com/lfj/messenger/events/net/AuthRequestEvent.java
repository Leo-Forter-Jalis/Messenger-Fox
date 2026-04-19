package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;

public record AuthRequestEvent(
        String email,
        String password
) implements Event {
}
