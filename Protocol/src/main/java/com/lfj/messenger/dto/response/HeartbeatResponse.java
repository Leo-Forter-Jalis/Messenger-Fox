package com.lfj.messenger.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.HEARTBEAT_RESPONSE)
public record HeartbeatResponse (
    @JsonProperty("request_id") UUID requestId,
    @JsonProperty("instant") Instant instant
) implements Response {
    @JsonProperty("type")
    public String type(){
        return MessageTypeConstants.HEARTBEAT_RESPONSE;
    }
}
