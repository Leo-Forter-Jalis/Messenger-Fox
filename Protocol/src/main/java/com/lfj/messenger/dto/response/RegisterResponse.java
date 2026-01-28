package com.lfj.messenger.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.REGISTER_RESPONSE)
public record RegisterResponse(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("user")UserDTO user,
        @JsonProperty("is_register") boolean isRegister,
        @JsonProperty("istant") Instant instant
) implements Response {
    public String type(){
        return MessageTypeConstants.REGISTER_RESPONSE;
    }
}
