package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.datatype.client.Chat;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

public record CreateChatRequest (
        @JsonProperty("request_id")UUID requestId,
        @JsonProperty("chat_data") Chat chatData,
        @JsonProperty("instant") Instant instant
) implements Request {
    @JsonProperty("type")
    public String type(){ return MessageTypeConstants.CREATE_CHAT_REQUEST; }
}
