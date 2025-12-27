package com.lfj.messager.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messager.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.REGISTER_REQUEST)
public record RegisterRequest(
        @JsonProperty("type") String type,
        @JsonProperty("request_id")UUID requestId,
        @JsonProperty("user_name")String userName,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password,
        @JsonProperty("instant") Instant instant
) implements Request {
    public RegisterRequest{
        if(!type.equals(MessageTypeConstants.REGISTER_REQUEST)) throw new IllegalArgumentException("Invalid type from RegisterRequest");
        Objects.requireNonNull(requestId, "RequestId cannot be null"); Objects.requireNonNull(userName, "UserName cannot be null");
    }
}
