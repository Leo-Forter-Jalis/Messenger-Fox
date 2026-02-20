package com.lfj.messenger.dto.datatype.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.dev.annotations.NeedsRefactoring;
import com.lfj.dev.annotations.NotNull;
import com.lfj.messenger.dto.types.MessageType;

import java.time.Instant;
import java.util.UUID;

@NeedsRefactoring
public record MessageDTO(
        @NotNull @JsonProperty("message_id") UUID messageId,// PROBLEM: The identifier must be defined or modified by the server!
        @NotNull @JsonProperty("chat_id") UUID chatId,
        @NotNull @JsonProperty("sender") UserDTO sender,
        @NotNull @JsonProperty("message_type") MessageType messageType,
        @NotNull @JsonProperty("content") String content,
        @NotNull @JsonProperty("instant")Instant instant
) {
        public MessageDTO{
                if(messageId == null) throw new IllegalArgumentException("messageId is null");
                if(chatId == null) throw new IllegalArgumentException("chatId cannot be null");
                if(sender == null) throw new IllegalArgumentException("senderId is null");
                if(messageType == null) throw new IllegalArgumentException("messageType is null");
                if(content == null) throw new IllegalArgumentException("content is null");
                if(instant == null) instant = Instant.now();
        }
}
