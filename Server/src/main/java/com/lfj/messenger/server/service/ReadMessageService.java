package com.lfj.messenger.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lfj.messenger.dto.datatype.server.UserDTO;
import com.lfj.messenger.dto.request.GetMessageRequest;
import com.lfj.messenger.dto.response.Response;
import com.lfj.messenger.dto.response.ErrorResponse;
import com.lfj.messenger.dto.response.GetMessageResponse;
import com.lfj.messenger.server.dao.MessageDAO;
import com.lfj.messenger.server.dao.UserDAO;
import com.lfj.messenger.server.threadmanager.ThreadManager;
import com.lfj.messenger.time.Time;

import java.util.Set;
import java.util.concurrent.CompletableFuture;


public class ReadMessageService {
    private MessageDAO messageDAO;
    private UserDAO userDAO;
    private ThreadManager threadManager;

    private Logger logger;

    private ReadMessageService() {  }
    public ReadMessageService(UserDAO userDAO, MessageDAO messageDAO, ThreadManager threadManager){
        this.logger = LoggerFactory.getLogger(ReadMessageService.class);
        this.userDAO = userDAO;
        this.messageDAO = messageDAO;
        this.threadManager = threadManager;
    }
    public CompletableFuture<Response> g (GetMessageRequest request){
        Set<UserDTO> users = this.userDAO.users(this.messageDAO.getSenderId(request.chatId(), request.limit()).get());
        return CompletableFuture.supplyAsync(this.messageDAO.readMessageAsync(request, users), this.threadManager.getIoExecutor())
                .thenApplyAsync(optional ->{
                    if(optional.isPresent()) return new GetMessageResponse(request.requestId(), optional.get(), Time.nowInstant());
                    else return new ErrorResponse(request.requestId(), (short) 200, "", Time.nowInstant());
                }, this.threadManager.getIoExecutor());
    }
}