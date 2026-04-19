package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;
import com.lfj.messfox.protocol.datatype.Message;

public record ReceiveMessageResponseEvent(Message message) implements Event {  }