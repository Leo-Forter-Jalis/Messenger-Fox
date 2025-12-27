package com.lfj.messager.shared.test;

import com.lfj.messager.codec.JsonCodec;
import com.lfj.messager.dto.Message;
import com.lfj.messager.dto.response.AuthResponse;
import com.lfj.messager.dto.response.ErrorResponse;
import com.lfj.messager.dto.types.MessageTypeConstants;
import com.lfj.messager.time.Time;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.UUID;

public class TestCodec {
    @Test
    public void test() throws IOException {
        String json = """
            {
                "type": "AUTH_RESPONSE",
                "request_id": "550e8400-e29b-41d4-a716-446655440000",
                "user": {
                    "user_id": "550e8400-e29b-41d4-a716-446655440001",
                    "email": "test@example.com",
                    "user_name": "test_user",
                    "create_at": "2024-01-09T10:24:00.123456Z"
                },
                "is_auth": true,
                "instant": "2024-01-15T10:30:00.123456Z"
            }
            """;
        String json1 = """
            {
                "type": "ERROR_RESPONSE",
                "request_id": "550e8400-e29b-41d4-a716-446655440000",
                "user": {
                    "user_id": "550e8400-e29b-41d4-a716-446655440001",
                    "email": "test@example.com",
                    "user_name": "test_user",
                    "create_at": "2024-01-09T10:24:00.123456Z"
                },
                "is_auth": true,
                "instant": "2024-01-15T10:30:00.123456Z"
            }
            """;
        try {
            Message message1 = JsonCodec.coder(json);
            System.out.println(message1);
            if(message1 instanceof AuthResponse response){
                System.out.println(response.type());
            }
        }catch (Exception e){
            System.err.println("Deserialization Error >> " + e.getMessage());
            System.out.println("Объект пропущен из-за ошибки десериализации!");
        }
        try{
            Message message = JsonCodec.coder(json);
            System.out.println(json);
            System.out.println(message);
        }catch (Exception e){
            System.err.println("Deserialization Error >> " + e.getMessage());
            System.out.println("Объект пропущен из-за ошибки десериализации!");
        }
        try {
            ErrorResponse error = new ErrorResponse(MessageTypeConstants.ERROR_RESPONSE, UUID.randomUUID(), (short) 404, "Not FF", Time.nowInstant());
            String message2 = JsonCodec.encoder(error, true);
            System.out.println(message2);
            Message message = JsonCodec.coder(message2);
            if(message instanceof ErrorResponse errorResponse) System.out.println(Time.getTime(errorResponse.instant()));
        }catch (Exception e){
            System.err.println("Deserialization Error >> " + e.getMessage());
            System.out.println("Объект пропущен из-за ошибки десериализации!");
        }
        Exception exception = assertThrows(Exception.class, () -> JsonCodec.coder(json1));
        System.out.println(exception.getMessage());

    }
}
