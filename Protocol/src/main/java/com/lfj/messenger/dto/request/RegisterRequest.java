package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.datatype.client.User;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.REGISTER_REQUEST)
public record RegisterRequest(
        @JsonProperty("request_id")UUID requestId,
        @JsonProperty("user_date") User user,
        @JsonProperty("instant") Instant instant
) implements Request {
    public String type(){ return MessageTypeConstants.REGISTER_REQUEST; }
    public RegisterRequest {
        Objects.requireNonNull(requestId, "RequestId cannot be null");
        Objects.requireNonNull(user, "User cannot be null");
        Objects.requireNonNull(instant, "Instant cannot be null");
    }
    public String displayName(){ return user.displayName(); }
    public String email(){ return user.email(); }
    public String password(){ return user.password(); }
}
