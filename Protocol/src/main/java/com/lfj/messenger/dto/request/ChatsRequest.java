package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

public record ChatsRequest(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("chatId") UUID chatId,
        @JsonProperty("instant") Instant instant
) implements Request {
    @JsonProperty("type")
    public String type(){ return MessageTypeConstants.CHATS_REQUEST; }
}
