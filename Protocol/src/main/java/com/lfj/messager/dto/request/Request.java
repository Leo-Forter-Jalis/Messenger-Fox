package com.lfj.messager.dto.request;

import com.lfj.messager.dto.Message;
public sealed interface Request extends Message permits AuthRequest, ChatsRequest, GetMessageRequest, MessageRequest, RegisterRequest {
}
