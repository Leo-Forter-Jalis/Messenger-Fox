package com.lfj.messenger.server.service;

import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.request.RegisterRequest;
import com.lfj.messenger.dto.response.ErrorResponse;
import com.lfj.messenger.dto.response.RegisterResponse;
import com.lfj.messenger.dto.response.Response;
import com.lfj.messenger.server.dao.UserDAO;
import com.lfj.messenger.server.threadmanager.ThreadManager;
import com.lfj.messenger.time.Time;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RegisterService {
    private UserDAO userDAO;
    private ThreadManager threadManager;
    public RegisterService(UserDAO userDAO, ThreadManager threadManager){
        this.userDAO = userDAO;
        this.threadManager = threadManager;
    }
    public CompletableFuture<Response> registrationAsync(RegisterRequest request){
        return CompletableFuture.supplyAsync(this.userDAO.registerAsync(request), this.threadManager.getCpuExecutor())
                .thenApplyAsync(optional ->{
                        if(optional.isPresent()) return register(request.requestId(), optional.get());
                        else return error(request.requestId());
                }, this.threadManager.getCpuExecutor());
    }
    private ErrorResponse error(UUID requestId){
        return new ErrorResponse(requestId, (short)300, "Failed registration", Time.nowInstant());
    }
    private RegisterResponse register(UUID requestId, UserDTO userDTO){
        return new RegisterResponse(requestId, userDTO, true, Time.nowInstant());
    }
}
