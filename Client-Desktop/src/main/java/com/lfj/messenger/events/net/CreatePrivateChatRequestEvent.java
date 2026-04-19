package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;
import java.util.UUID;

public record CreatePrivateChatRequestEvent(UUID userId) implements Event {  }
