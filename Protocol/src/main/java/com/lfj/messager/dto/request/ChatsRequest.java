package com.lfj.messager.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record ChatsRequest(
        @JsonProperty("type") String type,
        UUID requestId,
        Instant instant
) implements Request {
}
