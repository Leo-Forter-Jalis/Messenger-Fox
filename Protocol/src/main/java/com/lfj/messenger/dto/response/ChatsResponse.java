package com.lfj.messenger.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChatsResponse(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("users") List<UserDTO> users,
        @JsonProperty("instant") Instant instant
) implements Response {
    public String type(){
        return MessageTypeConstants.CHATS_RESPONSE;
    }
}
