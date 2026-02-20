package com.lfj.messenger.dto.datatype.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.dev.annotations.NeedsRefactoring;
import com.lfj.dev.annotations.NotNull;
import com.lfj.dev.annotations.Nullable;
import com.lfj.messenger.dto.types.ChatType;

import java.time.Instant;
import java.util.UUID;

@NeedsRefactoring
public record ChatDTO(
        @NotNull @JsonProperty("chat_id") UUID chatId,
        @NotNull @JsonProperty("chat_type") ChatType chatType,
        @Nullable @JsonProperty("chat_tag") String chatTag,
        @NotNull @JsonProperty("chat_name") String chatName,
        @NotNull @JsonProperty("create_at") Instant instant
        ) {
    @JsonCreator
    public ChatDTO {
        if(chatType != ChatType.PRIVATE && chatType != ChatType.GROUP) throw new IllegalArgumentException("Invalid chatType > " + chatType);
        if(chatId == null) throw new IllegalArgumentException("Invalid chatId is be null");
    }
}
