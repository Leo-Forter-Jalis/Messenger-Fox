package com.lfj.messenger.dto.datatype.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.dev.annotations.NotNull;
import com.lfj.messenger.dto.datatype.server.UserDTO;
import com.lfj.messenger.dto.types.MessageType;

import java.util.UUID;

public record Message (
        @NotNull @JsonProperty("chat_id") UUID chatId,
        @NotNull @JsonProperty("sender") UserDTO sender,
        @NotNull @JsonProperty("message_type") MessageType messageType,
        @NotNull @JsonProperty("content") String content
) {  }
