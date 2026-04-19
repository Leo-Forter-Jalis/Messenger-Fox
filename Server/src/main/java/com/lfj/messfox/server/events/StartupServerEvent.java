package com.lfj.messfox.server.events;

import com.lfj.messenger.eventbus.Event;

public record StartupServerEvent(boolean isTest) implements Event {  }
