package com.lfj.messager.dto.request;

import java.time.Instant;
import java.util.UUID;

public record GetMessageRequest(
        String type,
        UUID requestId,
        Instant instant
) implements Request {
}
