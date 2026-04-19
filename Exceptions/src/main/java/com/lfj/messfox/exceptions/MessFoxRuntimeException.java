package com.lfj.messfox.exceptions;

public class MessFoxRuntimeException extends RuntimeException {
    public MessFoxRuntimeException(String message) { super(message); }
    public MessFoxRuntimeException(String message, Throwable cause){ super(message, cause); }
}
