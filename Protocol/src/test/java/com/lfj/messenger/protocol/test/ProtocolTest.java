package com.lfj.messenger.protocol.test;

import com.lfj.messenger.dto.datatype.client.User;
import com.lfj.messenger.dto.datatype.server.MessageDTO;
import com.lfj.messenger.dto.datatype.client.Message;
import com.lfj.messenger.dto.datatype.server.UserDTO;
import com.lfj.messenger.dto.request.*;
import com.lfj.messenger.dto.request.GetMessageRequest.Direction;
import com.lfj.messenger.dto.response.*;
import com.lfj.messenger.dto.types.MessageType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ProtocolTest {
    @Test
    public void test(){
        assertDoesNotThrow(() ->{
            new RegisterRequest(UUID.randomUUID(), new User("display", "email", "password"), Instant.now());
            new RegisterResponse(UUID.randomUUID(), new UserDTO(UUID.randomUUID(), "userName", "uuu", Instant.now()), true, Instant.now());
            new AuthRequest(UUID.randomUUID(),"lol@gmail.com", "uuu", Instant.now());
            new AuthResponse(UUID.randomUUID(), null, false, Instant.now());
            new GetMessageRequest(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), Direction.NEWER, 10 ,Instant.now());
            new GetMessageResponse(UUID.randomUUID(), List.of(), Instant.now());
            new ChatsRequest(UUID.randomUUID(), UUID.randomUUID(), Instant.now());
            new ChatsResponse(UUID.randomUUID(), null, null, Instant.now());
            new MessageRequest(UUID.randomUUID(), new Message(null, null, null, null), Instant.now());
            new MessageResponse(UUID.randomUUID(), new MessageDTO(UUID.randomUUID(), null, new UserDTO(UUID.randomUUID(), "TEST", "TEST", Instant.now()), MessageType.TEXT, "АХАХАХАХХА", null), Instant.now());
        });
    }
    @Test
    public void test2(){
        assertThrows(Exception.class, ()->{new AuthRequest(null,"lol@gmail.com", "uuu", Instant.now());});
    }
}
