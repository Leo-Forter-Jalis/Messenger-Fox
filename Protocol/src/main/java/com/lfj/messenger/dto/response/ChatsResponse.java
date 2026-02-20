package com.lfj.messenger.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messenger.dto.datatype.server.ChatDTO;
import com.lfj.messenger.dto.datatype.server.ChatMemberDTO;
import com.lfj.messenger.dto.datatype.server.MessageDTO;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

public record ChatsResponse(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("chat") ChatDTO chat,
        @JsonProperty("members") ChatMemberDTO members,
        //@JsonProperty("last_message") MessageDTO message,
        @JsonProperty("instant") Instant instant
) implements Response {
    public String type(){return MessageTypeConstants.CHATS_RESPONSE;}
}
