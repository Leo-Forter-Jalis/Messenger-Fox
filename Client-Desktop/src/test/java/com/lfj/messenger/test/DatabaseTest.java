package com.lfj.messenger.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.lfj.messenger.base.db.Database;

import java.sql.SQLException;
import java.util.UUID;

public class DatabaseTest {
    @Test
    public void testOne(){
        assertDoesNotThrow(() ->{new Database(UUID.randomUUID());});
    }
}
