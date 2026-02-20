package com.lfj.messenger.server.service;

import com.lfj.messenger.dto.datatype.server.UserDTO;
import com.lfj.messenger.dto.request.AuthRequest;
import com.lfj.messenger.dto.response.AuthResponse;
import com.lfj.messenger.dto.response.ErrorResponse;
import com.lfj.messenger.dto.response.Response;
import com.lfj.messenger.server.dao.UserDAO;
import com.lfj.messenger.server.threadmanager.ThreadManager;
import com.lfj.messenger.time.Time;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private UserDAO userDAO;
    private ThreadManager threadManager;
    private AuthService() {  }
    public AuthService(UserDAO userDAO, ThreadManager threadManager) {
        this.userDAO = userDAO;
        this.threadManager = threadManager;
    }
    public CompletableFuture<Response> authorizationAsync(AuthRequest request) {
        return CompletableFuture.supplyAsync(userDAO.authorizationAsync(request), this.threadManager.getCpuExecutor())
                .thenApplyAsync(optional ->{
                    UserDTO dto = optional.orElse(null);
                    if(dto == null) return error(request.requestId());
                    return auth(request.requestId(), dto);
                }, this.threadManager.getCpuExecutor());
    }
    private ErrorResponse error (UUID request){
        return new ErrorResponse(request, (short) 300, "Error", Time.nowInstant());
    }
    private AuthResponse auth(UUID request, UserDTO userDTO){
        return new AuthResponse(request, userDTO, userDTO != null, Time.nowInstant());
    }
}