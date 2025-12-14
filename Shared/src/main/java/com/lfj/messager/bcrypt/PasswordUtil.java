package com.lfj.messager.bcrypt;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private static final int COST = 15;
    private PasswordUtil(){
        throw new UnsupportedOperationException("Utility class");
    }
    public static String hashPassword(String password){
        if(password == null || password.isEmpty()) throw new IllegalArgumentException("Password must not be null or empty");
        return BCrypt.hashpw(password, BCrypt.gensalt(COST));
    }
    public static boolean validPassword(String password, String hashed){
        return BCrypt.checkpw(password, hashed);
    }
}
