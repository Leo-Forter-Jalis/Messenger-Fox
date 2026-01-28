package com.lfj.messenger.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.datatype.MessageDTO;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("message") MessageDTO message,
        @JsonProperty("instant") Instant instant
) implements Response {
    public MessageResponse{
        if(requestId == null) throw new IllegalArgumentException("requestId is null");
        if(message == null) throw new IllegalArgumentException("message is null");
        if(instant == null) throw new IllegalArgumentException("instant is null");
    }
    public UUID getSenderId(){ return this.message.senderId(); }
    public UUID getReceiverId(){ return this.message.receiverId(); }
    public String type(){ return MessageTypeConstants.MESSAGE_RESPONSE; }
}
