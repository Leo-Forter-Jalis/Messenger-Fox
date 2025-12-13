package com.lfj.messager.dto.response;


/*
 * MessFox - Custom Messenger
 * The MIT License (MIT)
 * Copyright © 2025 Leo Forter Jalis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.lfj.messager.dto.type.MessageTypeConstants;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonTypeName(MessageTypeConstants.ERROR_RESPONSE)
public record ErrorResponse(
        @JsonProperty(value = "type") String type,
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("error_code") short errorCode,
        @JsonProperty("error_message") String errorMessage,
        @JsonProperty("instant") Instant instant
) implements Response {
    public ErrorResponse{
        if(!type.equals(MessageTypeConstants.ERROR_RESPONSE)) throw new IllegalArgumentException("Invalid type for ErrorResponse");
        Objects.requireNonNull(requestId, "RequestId cannot be null"); Objects.requireNonNull(errorMessage, "ErrorMessage cannot be null"); Objects.requireNonNull(instant, "Instant cannot be null");
    }
}
