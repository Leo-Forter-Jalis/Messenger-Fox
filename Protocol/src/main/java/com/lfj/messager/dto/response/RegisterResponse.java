package com.lfj.messager.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messager.dto.datatype.UserDTO;
import com.lfj.messager.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.REGISTER_RESPONSE)
public record RegisterResponse(
        String type,
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("user")UserDTO user,
        @JsonProperty("is_register") boolean isRegister,
        @JsonProperty("istant") Instant instant
) implements Response {

}
