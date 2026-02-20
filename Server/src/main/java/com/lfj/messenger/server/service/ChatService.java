package com.lfj.messenger.server.service;

import com.lfj.messenger.dto.datatype.server.ChatMemberDTO;
import com.lfj.messenger.dto.datatype.server.UserDTO;
import com.lfj.messenger.dto.request.ChatsRequest;
import com.lfj.messenger.dto.request.CreateChatRequest;
import com.lfj.messenger.dto.response.ChatsResponse;
import com.lfj.messenger.dto.response.CreatedChatResponse;
import com.lfj.messenger.dto.response.ErrorResponse;
import com.lfj.messenger.dto.response.Response;
import com.lfj.messenger.server.dao.ChatDAO;
import com.lfj.messenger.server.dao.ChatMemberDAO;
import com.lfj.messenger.server.dao.UserDAO;
import com.lfj.messenger.server.threadmanager.ThreadManager;
import com.lfj.messenger.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ChatService {
    private UserDAO userDAO;
    private ChatDAO chatDAO;
    private ChatMemberDAO chatMemberDAO;
    private ThreadManager threadManager;
    private Logger logger;
    private ChatService() {  }
    public ChatService(UserDAO userDAO, ChatDAO chatDAO, ChatMemberDAO chatMemberDAO, ThreadManager threadManager){
        this.logger = LoggerFactory.getLogger(ChatService.class);
        this.userDAO = userDAO;
        this.chatDAO = chatDAO;
        this.chatMemberDAO = chatMemberDAO;
        this.threadManager = threadManager;
    }
    public CompletableFuture<Response> createChatAsync(CreateChatRequest request){
        return CompletableFuture.supplyAsync(this.chatDAO.createChatAsync(request), this.threadManager.getIoExecutor())
                .thenApplyAsync(optional ->{
                    if(optional.isPresent()) {
                        this.chatMemberDAO.addMemberForPrivateChat(optional.get().chatId(), new HashSet<>(request.chatData().members()));
                        return new CreatedChatResponse(request.requestId(), optional.get(), Time.nowInstant());
                    }
                    else return new ErrorResponse(request.requestId(), (short) 100, "Error created chat...", Time.nowInstant());
                }, this.threadManager.getIoExecutor());
    }
    public CompletableFuture<Response> getChatAsync(ChatsRequest request){
        return CompletableFuture.supplyAsync(this.chatDAO.selectByChatIdAsync(request.chatId()), this.threadManager.getIoExecutor())
                .thenApplyAsync(optional ->{
                    AtomicReference<Response> response = new AtomicReference<>();
                    optional.ifPresentOrElse(chatDTO -> {
                        Set<UserDTO> users = this.userDAO.users(this.chatMemberDAO.getUsersId(chatDTO.chatId()).orElse(new HashSet<>()));
                        Optional<ChatMemberDTO> memberDTO = this.chatMemberDAO.selectChatMembersById(chatDTO.chatId(), users);
                        memberDTO.ifPresentOrElse(chatMemberDTO -> response.set(new ChatsResponse(request.requestId(), chatDTO, chatMemberDTO, Time.nowInstant())),
                                () -> response.set(new ErrorResponse(request.requestId(), (short) 101, "Chat members is empty", Time.nowInstant())));
                    }, () -> response.set(new ErrorResponse(request.requestId(), (short) 102, "Chat is not found", Time.nowInstant())));
                    return response.get();
                }, this.threadManager.getIoExecutor());
    }
    public Set<UUID> getUsersId(UUID chatId){
        return this.chatMemberDAO.getUsersId(chatId).orElse(new HashSet<>());
    }
}