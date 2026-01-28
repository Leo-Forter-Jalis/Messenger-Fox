package com.lfj.messenger.server.service;

import com.lfj.messenger.dto.request.MessageRequest;
import com.lfj.messenger.dto.response.ErrorResponse;
import com.lfj.messenger.dto.response.MessageResponse;
import com.lfj.messenger.dto.response.Response;
import com.lfj.messenger.server.dao.MessageDAO;
import com.lfj.messenger.server.threadmanager.ThreadManager;
import com.lfj.messenger.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class MessageSendService {
    private MessageDAO messageDAO;
    private ThreadManager threadManager;
    private Logger logger;
    private MessageSendService(){  }
    public MessageSendService(MessageDAO messageDAO, ThreadManager threadManager){
        this.logger = LoggerFactory.getLogger(MessageSendService.class);
        this.messageDAO = messageDAO;
        this.threadManager = threadManager;
    }
    public CompletableFuture<Response> sendMessage(MessageRequest request){
        return CompletableFuture.supplyAsync(messageDAO.writeAndSendMessageAsync(request), this.threadManager.getIoExecutor())
                .thenApplyAsync(optional -> {
                    if(optional.isPresent()) return optional.get();
                    else return new ErrorResponse(request.requestId(), (short) 300, "Error sending message", Time.nowInstant());
                }, this.threadManager.getIoExecutor());
    }
}
