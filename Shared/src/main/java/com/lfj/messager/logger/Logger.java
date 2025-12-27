package com.lfj.messager.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class Logger {
    private final org.apache.logging.log4j.Logger logger;
    private static final String FQCN = Logger.class.getName();
    private Logger(){
        logger = LogManager.getLogger();
    }
    public Logger(String name){
        logger = LogManager.getLogger(name);
    }
    public Logger(Class<?> clazz){
        logger = LogManager.getLogger(clazz);
    }
    public void log(Level level, String message){
        StackTraceElement caller = StackLocatorUtil.calcLocation(FQCN);
        String callerClass = caller.getClassName();
        String callerMethod = caller.getMethodName();
        String locationInfo = String.format("%s.%s", getSimpleName(callerClass), callerMethod);
        String fullMessage = String.format("[Thread:%s | %s] %s >> %s", Thread.currentThread().getName(), locationInfo, level.name(), message);
        if(logger instanceof AbstractLogger logger) logger.logIfEnabled(FQCN, level, null, fullMessage, (Throwable) null);
        else logger.log(level, message);
    }
    private String getSimpleName(String className){
        int lastDot = className.lastIndexOf('.');
        return (lastDot != -1) ? className.substring(lastDot + 1) : className;
    }
    public void log(Level level, Message message){
        if(logger == null) return;
        logger.log(level, message);
    }
}
