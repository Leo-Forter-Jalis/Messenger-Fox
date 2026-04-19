package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;
import com.lfj.messfox.protocol.datatype.Chat;

public record CreateChatResponseEvent (Chat chat) implements Event {  }
