package com.lfj.messenger.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.dev.annotations.NeedsRefactoring;
import com.lfj.dev.annotations.Nullable;
import com.lfj.messenger.dto.types.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;

@NeedsRefactoring
public record GetMessageRequest(
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("chat_id") UUID chatId,
        @Nullable @JsonProperty("cursor_message_id") UUID cursorMessageId,
        @JsonProperty("direction") Direction direction,
        @JsonProperty("limit") int limit,
        @JsonProperty("instant") Instant instant
) implements Request {
    public GetMessageRequest{
        if(cursorMessageId == null && direction == Direction.OLDER) throw new NullPointerException("cursorMessageId cannot be null in case Direction is OLDER");
    }
    public String type(){
        return MessageTypeConstants.GET_MESSAGE_REQUEST;
    }
    public enum Direction{
        NEWER,
        OLDER
    }
}
