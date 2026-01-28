package com.lfj.messenger.server.threadmanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    private ExecutorService cpuExecutor;
    private ExecutorService ioExecutor;
    public ThreadManager(){
        this.cpuExecutor = Executors.newFixedThreadPool(15);
        this.ioExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public ExecutorService getCpuExecutor() { return this.cpuExecutor; }
    public ExecutorService getIoExecutor() { return this.ioExecutor; }
    public void shutdown(){ this.cpuExecutor.shutdown(); this.ioExecutor.shutdown(); }
}
