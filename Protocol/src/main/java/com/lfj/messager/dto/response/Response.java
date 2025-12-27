package com.lfj.messager.dto.response;

import com.lfj.messager.dto.Message;
public sealed interface Response extends Message permits AuthResponse, ChatsResponse, ErrorResponse, GetMessageResponse, MessageResponse, RegisterResponse {
}
