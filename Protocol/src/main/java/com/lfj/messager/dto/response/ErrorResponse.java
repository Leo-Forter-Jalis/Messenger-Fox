package com.lfj.messager.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.lfj.messager.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.ERROR_RESPONSE)
public record ErrorResponse(
        @JsonProperty(value = "type") String type,
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("error_code") short errorCode,
        @JsonProperty("error_message") String errorMessage,
        @JsonProperty("instant") Instant instant
) implements Response {
    public ErrorResponse{
        if(!type.equals(MessageTypeConstants.ERROR_RESPONSE)) throw new IllegalArgumentException("Invalid type for ErrorResponse");
        Objects.requireNonNull(requestId, "RequestId cannot be null"); Objects.requireNonNull(errorMessage, "ErrorMessage cannot be null"); Objects.requireNonNull(instant, "Instant cannot be null");
    }
}
