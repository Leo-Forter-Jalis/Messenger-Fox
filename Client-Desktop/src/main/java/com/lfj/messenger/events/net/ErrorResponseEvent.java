package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;

public record ErrorResponseEvent(Throwable throwable) implements Event {  }