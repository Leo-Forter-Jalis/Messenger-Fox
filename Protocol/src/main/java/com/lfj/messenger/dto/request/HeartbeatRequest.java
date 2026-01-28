package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.HEARTBEAT_REQUEST)
public record HeartbeatRequest (
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("instant") Instant instant
) implements Request {
    @JsonCreator
    public HeartbeatRequest{

    }
    @JsonProperty("type")
    public String type(){
        return MessageTypeConstants.HEARTBEAT_REQUEST;
    }
}
