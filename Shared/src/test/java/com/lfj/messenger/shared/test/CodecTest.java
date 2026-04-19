package com.lfj.messenger.shared.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lfj.messfox.shared.codec.JsonCodec;
import com.lfj.messfox.protocol.request.AuthRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CodecTest {
    @Test
    public void test() throws Exception {
        AuthRequest authRequest = new AuthRequest(UUID.randomUUID(), "Example", "1234", Instant.now());
        JsonCodec codec = new JsonCodec();
        byte[] bytes = new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsBytes(authRequest);
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        ChannelHandlerContext ctx = null;
        List<Object> list = new ArrayList<>();
        codec.decode(ctx, byteBuf, list);
        IO.println(list.getLast());
    }
}
