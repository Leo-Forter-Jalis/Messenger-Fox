package com.lfj.messager.dto.datatype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record UserDTO(
        @JsonProperty("user_id") UUID userId,
        @JsonProperty("user_name") String userName,
        @JsonProperty("email") String email,
        @JsonProperty("create_at") Instant createAt
) {
    @JsonCreator
    public UserDTO{
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(userName, "userName cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(createAt, "createAt cannot be null");
    }
}
