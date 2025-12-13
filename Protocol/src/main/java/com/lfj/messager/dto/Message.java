package com.lfj.messager.dto;

/*
 * MessFox - Custom Messenger
 * The MIT License (MIT)
 * Copyright © 2025 Leo Forter Jalis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.lfj.messager.dto.request.*;
import com.lfj.messager.dto.response.*;
import com.lfj.messager.dto.type.MessageTypeConstants;

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
