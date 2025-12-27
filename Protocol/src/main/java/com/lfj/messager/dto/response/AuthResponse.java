package com.lfj.messager.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

import com.lfj.messager.dto.datatype.UserDTO;
import com.lfj.messager.dto.types.MessageTypeConstants;

@JsonTypeName(MessageTypeConstants.AUTH_RESPONSE)
public record AuthResponse(
        @JsonProperty("type") String type,
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("user") UserDTO user,
        @JsonProperty("is_auth") boolean isAuth,
        @JsonProperty("instant") Instant instant
) implements Response {
    public AuthResponse{
        if(!type.equals(MessageTypeConstants.AUTH_RESPONSE)) throw new IllegalArgumentException("Error");
    }
}
