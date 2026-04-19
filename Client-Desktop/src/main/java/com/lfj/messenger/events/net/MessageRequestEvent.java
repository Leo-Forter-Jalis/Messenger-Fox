package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;

import java.util.UUID;

public record MessageRequestEvent(
        UUID chatId,
        String message
) implements Event {
}
