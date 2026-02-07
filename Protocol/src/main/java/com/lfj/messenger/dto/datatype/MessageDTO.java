package com.lfj.messenger.dto.datatype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.dev.annotations.NeedsRefactoring;

import java.time.Instant;
import java.util.UUID;

@NeedsRefactoring
public record MessageDTO(
        @JsonProperty("message_id") UUID messageId, // PROBLEM: The identifier must be defined or modified by the server!
        @JsonProperty("chat_id") UUID chatId, // Freeze or болт забит
        @JsonProperty("sender") UserDTO sender,
        @JsonProperty("receiver_id") UUID receiverId,
        @JsonProperty("message_type") String messageType,
        @JsonProperty("content") String content,
        @JsonProperty("instant")Instant instant
) {
        public MessageDTO{
                if(messageId == null) throw new IllegalArgumentException("messageId is null");
                if(chatId == null) System.out.println("Freeze");
                if(sender == null) throw new IllegalArgumentException("senderId is null");
                if(receiverId == null) throw new IllegalArgumentException("receiverId is null");
                if(messageType == null) throw new IllegalArgumentException("messageType is null");
                if(content == null) throw new IllegalArgumentException("content is null");
                if(instant == null) instant = Instant.now();
        }
}
