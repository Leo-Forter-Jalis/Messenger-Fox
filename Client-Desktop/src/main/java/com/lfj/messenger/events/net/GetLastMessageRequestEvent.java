package com.lfj.messenger.events.net;

import com.lfj.messenger.eventbus.Event;

import java.util.UUID;

public record GetLastMessageRequestEvent(UUID chatId) implements Event {  }
