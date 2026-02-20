package com.lfj.messenger.dto.request;

import com.lfj.messenger.dto.Message;
public sealed interface Request extends Message permits AuthRequest, ChatsRequest, CreateChatRequest, GetMessageRequest, HeartbeatRequest, MessageRequest, RegisterRequest {  }
