package com.lfj.messenger.codec;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lfj.messenger.dto.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.security.InvalidParameterException;
import java.util.List;

public class JsonCodec extends ByteToMessageCodec<Message> {
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule());
    private static final int MAX_LENGTH = 1024 * 1024;
    private static final Logger logger = LoggerFactory.getLogger(JsonCodec.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        logger.info("Config receiving message");
        if(byteBuf.readableBytes() < 4){
            logger.info("readableBytes < 4. Returned...");
            return;
        }
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt();
        if(length > MAX_LENGTH || length < 0){
            logger.info("Jopa");
            throw new InvalidParameterException(String.format("Frame length to invalid value: %d", length));
        }
        if(byteBuf.readableBytes() < length){
            logger.info("Length > readableBytes. Returned...");
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        try {
            Message message = MAPPER.readValue(bytes, Message.class);
            out.add(message);
        }catch (Exception e){
            ctx.fireExceptionCaught(e);
        }
        logger.info("De");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, ByteBuf byteBuf) {
        logger.info("Config sending message");
        try {
            byte[] bytes = MAPPER.writeValueAsBytes(message);
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        }catch (Exception e){
            ctx.fireExceptionCaught(e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        System.out.println(cause);
        cause.printStackTrace();
    }

}
