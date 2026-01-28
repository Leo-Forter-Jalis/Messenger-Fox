package com.lfj.messenger.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

public record GetMessageResponse(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("instant") Instant instant
) implements Response {
    public String type(){
        return MessageTypeConstants.GET_MESSAGE_RESPONSE;
    }
}
