package com.lfj.messenger.dto.datatype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.types.ChatType;

import java.util.UUID;

public record ChatDTO(
        @JsonProperty("chat_type") ChatType chatType,
        @JsonProperty("chat_id") UUID chatId
) {
    @JsonCreator
    public ChatDTO {
        if(chatType != ChatType.PRIVATE && chatType != ChatType.GROUP) throw new IllegalArgumentException("Invalid chatType > " + chatType);
        if(chatId == null) throw new IllegalArgumentException("Invalid chatId is be null");
    }
}
