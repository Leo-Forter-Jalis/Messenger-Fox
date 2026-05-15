package com.lfj.messenger.bcrypt;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PasswordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);
    private static final int COST = 15;
    private PasswordUtil(){
        throw new UnsupportedOperationException("Utility class");
    }
    public static String hashPassword(String password){
        if(password == null || password.isEmpty()) throw new IllegalArgumentException("Password must not be null or empty");

        long start = System.currentTimeMillis();
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(COST));
        long end = System.currentTimeMillis();
        logger.debug("Encryption took >> {}ms", end - start);
        return hashed;
    }
    public static boolean validPassword(String password, String hashed){
        return BCrypt.checkpw(password, hashed);
    }
}
