package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

public record GetMessageRequest(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("instant") Instant instant
) implements Request {
    public String type(){
        return MessageTypeConstants.GET_MESSAGE_REQUEST;
    }
}
