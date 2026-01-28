package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.REGISTER_REQUEST)
public record RegisterRequest(
        @JsonProperty("request_id")UUID requestId,
        @JsonProperty("display_name") String displayName,
        @JsonProperty("user_name")String userName,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password,
        @JsonProperty("instant") Instant instant
) implements Request {
    public String type(){
        return MessageTypeConstants.REGISTER_REQUEST;
    }
    public RegisterRequest{
        if(displayName == null) displayName = userName;
        Objects.requireNonNull(requestId, "RequestId cannot be null"); Objects.requireNonNull(userName, "UserName cannot be null");
    }

}
