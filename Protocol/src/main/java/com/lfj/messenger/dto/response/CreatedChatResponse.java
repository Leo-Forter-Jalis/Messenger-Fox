package com.lfj.messenger.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.datatype.server.ChatDTO;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

public record CreatedChatResponse(
        @JsonProperty("request_id")UUID requestId,
        @JsonProperty("chat") ChatDTO chatDTO,
        @JsonProperty("instant") Instant instant
) implements Response {
    @JsonProperty("type")
    public String type(){ return MessageTypeConstants.CREATED_CHAT_RESPONSE; }
}
