package com.lfj.messenger.shared.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.lfj.messenger.bcrypt.PasswordUtil;

public class TestBCrypt {
    @Test
    public void test(){
        String password = "userName";
        String hashed = PasswordUtil.hashPassword(password);
        System.out.printf("Password > '%s' hashed to '%s'\n", password, hashed);
        System.out.printf("Valid password '%s' > %b", password, PasswordUtil.validPassword(password, hashed));
        assertTrue(PasswordUtil.validPassword(password, hashed));
    }
    @Test
    public void test2(){
        String password = "UGU-UNY-AUU";
        String hashed = PasswordUtil.hashPassword(password);
        System.out.printf("Password > '%s' hashed to '%s'\n", password, hashed);
        System.out.printf("Valid password '%s' > %b", password, PasswordUtil.validPassword("ugu-uny-auu", hashed));
    }
}
