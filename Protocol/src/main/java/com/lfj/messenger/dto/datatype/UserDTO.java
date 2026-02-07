package com.lfj.messenger.dto.datatype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lfj.dev.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record UserDTO(
        @JsonProperty("user_id") UUID userId,
        @JsonProperty("display_name") String displayName,
        @Nullable @JsonProperty("user_name") String userName,
        @JsonProperty("create_at") Instant createAt
) {
    @JsonCreator
    public UserDTO{
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(displayName, "userName cannot be null");
        Objects.requireNonNull(createAt, "createAt cannot be null");
    }
}
