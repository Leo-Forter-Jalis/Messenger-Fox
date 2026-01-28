package com.lfj.messenger.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.types.MessageTypeConstants;

@JsonTypeName(MessageTypeConstants.AUTH_RESPONSE)
public record AuthResponse(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("user") UserDTO user,
        @JsonProperty("is_auth") boolean isAuth,
        @JsonProperty("instant") Instant instant
) implements Response {
    public AuthResponse{

    }
    public String type(){
        return MessageTypeConstants.AUTH_RESPONSE;
    }
}
