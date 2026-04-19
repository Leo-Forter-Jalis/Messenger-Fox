package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;
import com.lfj.messfox.protocol.datatype.User;

public record SetUsernameResponseEvent(User user) implements Event {  }
