package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.types.MessageTypeConstants;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.AUTH_REQUEST)
public record AuthRequest(
    @JsonProperty("request_id") UUID requestId,
    @JsonProperty("email") String email,
    @JsonProperty("password") String password,
    @JsonProperty("instant") Instant instant
) implements Request {
    public AuthRequest{
        Objects.requireNonNull(requestId, "RequestId cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
        Objects.requireNonNull(instant, "Instant cannot be null");
    }
    @JsonProperty("type")
    public String type(){
        return MessageTypeConstants.AUTH_REQUEST;
    }
}
