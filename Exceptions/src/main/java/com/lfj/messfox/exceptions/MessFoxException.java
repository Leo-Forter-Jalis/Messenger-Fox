package com.lfj.messfox.exceptions;

public class MessFoxException extends Exception {
    public MessFoxException(String message) { super(message); }
    public MessFoxException(String message, Throwable cause){ super(message, cause); }
}
