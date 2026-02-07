package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.lfj.messenger.dto.datatype.MessageDTO;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.response.MessageResponse;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.MESSAGE_REQUEST)
public record MessageRequest(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("message") MessageDTO message,
        @JsonProperty("instant") Instant instant
) implements Request {
    public UserDTO getSender(){ return message.sender(); }
    public UUID getChatId(){ return message.chatId(); }
    public String type(){ return MessageTypeConstants.MESSAGE_REQUEST; }
    public MessageResponse convertToResponse(){
        return new MessageResponse(requestId, message, instant);
    }
}
