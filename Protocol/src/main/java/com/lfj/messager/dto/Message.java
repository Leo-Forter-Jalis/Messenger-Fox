package com.lfj.messager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.lfj.messager.dto.request.*;
import com.lfj.messager.dto.response.*;
import com.lfj.messager.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use= JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = ErrorResponse.class
)
@JsonSubTypes({
        @Type(value= AuthRequest.class, name = MessageTypeConstants.AUTH_REQUEST),
        @Type(value= AuthResponse.class, name= MessageTypeConstants.AUTH_RESPONSE),
        @Type(value= RegisterRequest.class, name= MessageTypeConstants.REGISTER_REQUEST),
        @Type(value= RegisterResponse.class, name= MessageTypeConstants.REGISTER_RESPONSE),
        @Type(value= MessageRequest.class, name= MessageTypeConstants.MESSAGE_REQUEST),
        @Type(value= MessageResponse.class, name= MessageTypeConstants.MESSAGE_RESPONSE),
        @Type(value= ChatsRequest.class, name= MessageTypeConstants.CHATS_REQUEST),
        @Type(value= ChatsResponse.class, name= MessageTypeConstants.CHATS_RESPONSE),
        @Type(value= GetMessageRequest.class, name= MessageTypeConstants.GET_MESSAGE_REQUEST),
        @Type(value= GetMessageResponse.class, name= MessageTypeConstants.GET_MESSAGE_RESPONSE),
        @Type(value= ErrorResponse.class, name= MessageTypeConstants.ERROR_RESPONSE)
})
public interface Message {
    @JsonProperty(value= "type", access = JsonProperty.Access.WRITE_ONLY) String type();
    UUID requestId();
    Instant instant();
}
