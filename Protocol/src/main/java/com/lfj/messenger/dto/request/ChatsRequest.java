package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChatsRequest(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("instant") Instant instant
) implements Request {
    @JsonProperty("type")
    public String type(){ return MessageTypeConstants.CHATS_REQUEST; }
}
