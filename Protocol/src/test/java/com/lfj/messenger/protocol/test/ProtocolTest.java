package com.lfj.messenger.protocol.test;

import com.lfj.messenger.dto.datatype.MessageDTO;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.request.*;
import com.lfj.messenger.dto.response.*;
import com.lfj.messenger.dto.types.MessageType;
import com.lfj.messenger.dto.types.MessageTypeConstants;
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
            new RegisterRequest(UUID.randomUUID(), "display", "username", "lol@gmail.com", "uuu", Instant.now());
            new RegisterResponse(UUID.randomUUID(), new UserDTO(UUID.randomUUID(), "userName", "uuu", "lol@gmail.com", Instant.now()), true, Instant.now());
            new AuthRequest(UUID.randomUUID(),"lol@gmail.com", "uuu", Instant.now());
            new AuthResponse(UUID.randomUUID(), null, false, Instant.now());
            new GetMessageRequest(UUID.randomUUID(), Instant.now());
            new GetMessageResponse(UUID.randomUUID(), Instant.now());
            new ChatsRequest(UUID.randomUUID(), Instant.now());
            new ChatsResponse(UUID.randomUUID(), List.of(), Instant.now());
            new MessageRequest(UUID.randomUUID(), new MessageDTO(UUID.randomUUID(), null, UUID.randomUUID(), UUID.randomUUID(), MessageType.TEXT.name(), "", Instant.now()),Instant.now());
            new MessageResponse(UUID.randomUUID(), new MessageDTO(UUID.randomUUID(), null, UUID.randomUUID(), UUID.randomUUID(), MessageType.TEXT.name(), "АХАХАХАХХА", null), Instant.now());
        });
    }
    @Test
    public void test2(){
        assertThrows(Exception.class, ()->{new AuthRequest(null,"lol@gmail.com", "uuu", Instant.now());});
    }
}
