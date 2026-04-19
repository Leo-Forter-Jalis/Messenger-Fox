package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;

import java.util.List;
import java.util.UUID;

public record CreateChatRequestEvent(
        String chatName
) implements Event {  }
