package com.lfj.messenger.uuid7;

import com.github.f4b6a3.uuid.UuidCreator;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UUIDv7 {
    private UUIDv7(){  }
    public static UUID next(){
        return UuidCreator.getTimeOrderedEpoch();
    }
    public static Optional<Instant> getTimestamp(UUID uuid){
        if(uuid.version() == 7){
            Instant instant = Instant.ofEpochMilli(uuid.getMostSignificantBits() >> 16);
            return Optional.ofNullable(instant);
        }
        return Optional.empty();
    }
}
