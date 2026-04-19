package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;
import com.lfj.messfox.protocol.datatype.Chat;

public record CreatePrivateChatResponseEvent(Chat chat) implements Event {  }
