package com.lfj.messenger.servertest;

import com.lfj.messenger.dto.datatype.client.Chat;
import com.lfj.messenger.dto.datatype.client.Message;
import com.lfj.messenger.dto.datatype.server.ChatDTO;
import com.lfj.messenger.dto.datatype.server.UserDTO;
import com.lfj.messenger.dto.request.*;
import com.lfj.messenger.dto.types.ChatType;
import com.lfj.messenger.dto.types.MessageType;
import com.lfj.messenger.time.Time;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.lfj.messenger.dto.datatype.client.User;
import com.lfj.messenger.server.dao.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class DTest {
    private DataSource dataSource;
    private UserDAO userDAO;
    private ChatDAO chatDAO;
    private MessageDAO messageDAO;
    private ChatMemberDAO chatMemberDAO;
    @BeforeEach
    public void start(){
        this.dataSource = DatabaseTest.getDataSource();
        this.userDAO = new UserDAO(dataSource);
        this.chatDAO = new ChatDAO(dataSource);
        this.messageDAO = new MessageDAO(dataSource);
        this.chatMemberDAO = new ChatMemberDAO(dataSource);
    }

    @Test
    public void test1(){
        Optional<UserDTO> User = this.userDAO.registerUser(new RegisterRequest(UUID.randomUUID(), new User("Leo_Forter_Jalis", "leo.test@example.com", "12345"), Time.nowInstant()));
        Optional<UserDTO> User1 = this.userDAO.registerUser(new RegisterRequest(UUID.randomUUID(), new User("Leo_Forter_Jalis", "leo.lol@example.com", "12345"), Time.nowInstant()));
        System.err.println(User);
        System.err.println(User1);
        Chat chat = new Chat(ChatType.PRIVATE, null, null, List.of(User.get().userId(), User1.get().userId()));
        Optional<ChatDTO> chatDTO = this.chatDAO.createChat(new CreateChatRequest(UUID.randomUUID(), chat, Time.nowInstant()));
        System.err.println(chatDTO);
        this.chatMemberDAO.addMemberForPrivateChat(chatDTO.get().chatId(), Set.of(User.get().userId(), User1.get().userId()));
        Message message = new Message(chatDTO.get().chatId(), User.get(), MessageType.TEXT, "Мяу");
        System.err.println(messageDAO.writeAndSendMessage(new MessageRequest(UUID.randomUUID(), message, Time.nowInstant())));
        Set<UserDTO> users = this.userDAO.users(messageDAO.getSenderId(chatDTO.get().chatId(), 10).get());
        System.err.println(messageDAO.readNewerMessage(new GetMessageRequest(UUID.randomUUID(), chatDTO.get().chatId(), null,GetMessageRequest.Direction.NEWER, 10, Time.nowInstant()), users).get());
        System.err.println(chatDAO.selectChatById(chatDTO.get().chatId()));
        System.err.println(chatMemberDAO.selectChatMembersById(chatDTO.get().chatId(), users));
    }
}