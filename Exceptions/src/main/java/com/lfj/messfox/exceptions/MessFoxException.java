package com.lfj.messfox.exceptions;

public class MessFoxException extends RuntimeException {
    public MessFoxException(String message) {
        super(message);
    }
    public MessFoxException(String message, Throwable cause){ super(message, cause); }
}
