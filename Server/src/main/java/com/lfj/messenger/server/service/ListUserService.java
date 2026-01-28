package com.lfj.messenger.server.service;

import com.lfj.messenger.dto.request.ChatsRequest;
import com.lfj.messenger.dto.response.ChatsResponse;
import com.lfj.messenger.dto.response.ErrorResponse;
import com.lfj.messenger.dto.response.Response;
import com.lfj.messenger.server.dao.UserDAO;
import com.lfj.messenger.server.threadmanager.ThreadManager;
import com.lfj.messenger.time.Time;

import java.util.concurrent.CompletableFuture;

public class ListUserService {
    private ThreadManager threadManager;
    private UserDAO userDAO;
    public ListUserService(ThreadManager threadManager, UserDAO userDAO){
        this.threadManager = threadManager;
        this.userDAO = userDAO;
    }
    public CompletableFuture<Response> listUsers(ChatsRequest request){
        return CompletableFuture.supplyAsync(userDAO.userList(request), threadManager.getIoExecutor())
                .thenApplyAsync(list ->{
                    if(list != null) return new ChatsResponse(request.requestId(), list, Time.nowInstant());
                    else return new ErrorResponse(request.requestId(), (short) 200, "Users list error", Time.nowInstant());
                }, threadManager.getIoExecutor());

    }
}
