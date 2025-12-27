package com.lfj.messager.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messager.dto.types.MessageTypeConstants;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.AUTH_REQUEST)
public record AuthRequest(
    @JsonProperty("type") String type,
    @JsonProperty("request_id") UUID requestId,
    @JsonProperty("email") String email,
    @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY) String password,
    @JsonProperty("instant") Instant instant
) implements Request {
    public AuthRequest{
        if(!type.equals(MessageTypeConstants.AUTH_REQUEST)) throw new IllegalArgumentException("Invalid type for AuthRequest");
        Objects.requireNonNull(type, "Type cannot be null"); Objects.requireNonNull(requestId, "RequestId cannot be null"); Objects.requireNonNull(email, "Email cannot be null"); Objects.requireNonNull(password, "Password cannot be null"); Objects.requireNonNull(instant, "Instant cannot be null");

    }
}
