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

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lfj.messager.dto.UserDTO;
import com.lfj.messager.dto.type.MessageTypeConstants;

import java.time.Instant;
import java.util.UUID;
@JsonTypeName(MessageTypeConstants.REGISTER_RESPONSE)
public record RegisterResponse(
        String type,
        @JsonProperty("request_id") UUID requestId,
        @JsonProperty("user")UserDTO user,
        @JsonProperty("is_register") boolean isRegister
) implements Response {
    private static Instant instant;
    public Instant instant(){
        return instant;
    }
}
