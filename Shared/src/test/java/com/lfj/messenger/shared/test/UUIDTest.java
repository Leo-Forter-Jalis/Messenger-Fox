package com.lfj.messenger.shared.test;

import com.lfj.messenger.time.Time;
import com.lfj.messenger.uuid7.UUIDv7;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UUIDTest {
    @Test
    public void testGetTimestamp(){
        UUID uuid = UUIDv7.next();
        IO.println("version >> " + uuid.version());
        IO.println("result >> " + UUIDv7.getTimestamp(uuid));
    }
}
