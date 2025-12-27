package com.lfj.messager.dto.datatype;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record MessageDTO(
        @JsonProperty("message_id") UUID messageId,
        @JsonProperty("chat_id") UUID chatId,
        @JsonProperty("sender_id") UUID senderId,
        @JsonProperty("message_type") String messageType,
        @JsonProperty("content") String content,
        @JsonProperty("instant")Instant instant
        ) {
}
