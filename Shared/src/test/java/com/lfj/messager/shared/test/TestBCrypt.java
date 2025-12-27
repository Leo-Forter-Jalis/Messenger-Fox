package com.lfj.messager.shared.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.lfj.messager.bcrypt.PasswordUtil;

public class TestBCrypt {
    @Test
    public void test(){
        String password = "userName";
        String hashed = PasswordUtil.hashPassword(password);
        System.out.printf("Password > '%s' hashed to '%s'\n", password, hashed);
        System.out.printf("Valid password '%s' > %b", password, PasswordUtil.validPassword(password, hashed));
        assertTrue(PasswordUtil.validPassword(password, hashed));
    }
}
