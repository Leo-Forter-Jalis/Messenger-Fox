package com.lfj.messager.protocol.test;

import com.lfj.messager.dto.datatype.UserDTO;
import com.lfj.messager.dto.types.MessageTypeConstants;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.lfj.messager.dto.request.*;
import com.lfj.messager.dto.response.*;

import java.time.Instant;
import java.util.UUID;

public class ProtocolTest {
    @Test
    public void test(){
        assertDoesNotThrow(() ->{
            new RegisterRequest(MessageTypeConstants.REGISTER_REQUEST, UUID.randomUUID(), "userName", "lol@gmail.com", "uuu", Instant.now());
            new RegisterResponse(MessageTypeConstants.REGISTER_RESPONSE, UUID.randomUUID(), new UserDTO(UUID.randomUUID(), "userName", "lol@gmail.com", Instant.now()), true, Instant.now());
            new AuthRequest(MessageTypeConstants.AUTH_REQUEST, UUID.randomUUID(),"lol@gmail.com", "uuu", Instant.now());
            new AuthResponse(MessageTypeConstants.AUTH_RESPONSE, UUID.randomUUID(), null, false, Instant.now());
        });
    }
    @Test
    public void test2(){
        assertThrows(Exception.class, ()->{new AuthRequest(MessageTypeConstants.AUTH_REQUEST, null,"lol@gmail.com", "uuu", Instant.now());});
    }
}
