package com.lfj.messager.codec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lfj.messager.dto.Message;

import java.io.IOException;

public class JsonCodec {
    private JsonCodec(){
    }
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    public static Message coder(byte[] bytes) throws IOException {
        return mapper.readValue(bytes, Message.class);
    }
    public static Message coder(String message) throws IOException{
        return mapper.readValue(message, Message.class);
    }
    public static byte[] encoder(Message message) throws JsonProcessingException {
        return mapper.writeValueAsBytes(message);
    }
    public static String encoder(Message message, boolean l)throws  JsonProcessingException{
        return mapper.writeValueAsString(message);
    }
}
