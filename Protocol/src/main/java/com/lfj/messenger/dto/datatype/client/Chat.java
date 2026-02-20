package com.lfj.messenger.dto.datatype.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.dev.annotations.NotNull;
import com.lfj.dev.annotations.Nullable;
import com.lfj.messenger.dto.types.ChatType;

import java.util.List;
import java.util.UUID;

public record Chat (
        @NotNull @JsonProperty("chat_type") ChatType chatType,
        @Nullable @JsonProperty("chat_name") String chatName,
        @Nullable @JsonProperty("chat_tag") String chatTag,
        @NotNull @JsonProperty("members") List<UUID> members
) {  }