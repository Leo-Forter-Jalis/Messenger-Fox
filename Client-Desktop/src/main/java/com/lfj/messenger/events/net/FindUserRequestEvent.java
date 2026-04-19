package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;

public record FindUserRequestEvent(String userName) implements Event {  }
