package com.lfj.messenger.dto.response;

import com.lfj.messenger.dto.Message;
public sealed interface Response extends Message permits AuthResponse, ChatsResponse, CreatedChatResponse, ErrorResponse, GetMessageResponse, HeartbeatResponse, MessageResponse, RegisterResponse {  }
