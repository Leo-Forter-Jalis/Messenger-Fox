package com.lfj.messenger.dto.datatype.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.dev.annotations.NotNull;

public record User (
        @NotNull @JsonProperty("display_name") String displayName,
        @NotNull @JsonProperty("email") String email,
        @NotNull @JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY) String password
) {  }
