package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;
import com.lfj.messfox.protocol.type.MessageType;

import java.util.UUID;

public record SentMessageRequestEvent(
        UUID chatId,
        MessageType messageType,
        String content
) implements Event {  }