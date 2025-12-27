package com.lfj.messager.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.lfj.messager.dto.datatype.MessageDTO;
import com.lfj.messager.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.MESSAGE_REQUEST)
public record MessageRequest(
        @JsonProperty("type") String type,
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("message") MessageDTO message,
        @JsonProperty("instant") Instant instant
) implements Request {
    public UUID getSenderId(){
        return message.senderId();
    }
    public UUID getChatId(){
        return message.chatId();
    }
}
