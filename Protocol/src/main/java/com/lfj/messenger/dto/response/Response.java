package com.lfj.messenger.dto.response;

import com.lfj.messenger.dto.Message;
public sealed interface Response extends Message permits AuthResponse, ChatsResponse, ErrorResponse, GetMessageResponse, MessageResponse, RegisterResponse, HeartbeatResponse {
}
