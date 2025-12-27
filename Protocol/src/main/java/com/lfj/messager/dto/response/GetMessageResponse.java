package com.lfj.messager.dto.response;

import java.time.Instant;
import java.util.UUID;

public record GetMessageResponse(
        String type,
        UUID requestId,
        Instant instant
) implements Response {
}
