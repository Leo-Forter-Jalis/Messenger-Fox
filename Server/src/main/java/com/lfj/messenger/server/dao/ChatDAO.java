package com.lfj.messenger.server.dao;

import com.lfj.messenger.dto.datatype.server.ChatDTO;
import com.lfj.messenger.dto.request.CreateChatRequest;
import com.lfj.messenger.dto.types.ChatType;
import com.lfj.messenger.time.Time;
import com.lfj.messenger.uuid7.UUIDv7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class ChatDAO {
    private DataSource dataSource;
    private Logger logger;
    private final static String INSERT = "INSERT INTO chats_table(chat_id, chat_type, chat_tag, chat_name, created_at) VALUES(?, ?, ?, ?, ?)";
    private final static String SELECT_BY_CHAT_TAG = "SELECT chat_id, chat_type, chat_tag, chat_name, created_at FROM chats_table WHERE chat_tag = ?";
    private final static String SELECT_BY_CHAT_ID = "SELECT chat_id, chat_type, chat_tag, chat_name, created_at FROM chats_table WHERE chat_id = ?";
    private ChatDAO() {  }
    public ChatDAO(DataSource dataSource) {
        this.logger = LoggerFactory.getLogger(ChatDAO.class);
        this.dataSource = dataSource;
    }
    public Supplier<Optional<ChatDTO>> createChatAsync(CreateChatRequest request){
        return () -> createChat(request);
    }
    public Supplier<Optional<ChatDTO>> selectByChatIdAsync(UUID chatId){
        return () -> selectChatById(chatId);
    }
    public Optional<ChatDTO> createChat(CreateChatRequest request){
        try(Connection connection = this.dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT)){
            UUID chatId = UUIDv7.next();
            Instant instant = Time.nowInstant();
            stmt.setObject(1, chatId);
            stmt.setString(2, request.chatData().chatType().name());
            stmt.setString(3, request.chatData().chatTag());
            stmt.setString(4, request.chatData().chatName());
            stmt.setTimestamp(5, Timestamp.from(instant));
            stmt.executeUpdate();
            return Optional.of(chat(chatId, request.chatData().chatType(), request.chatData().chatTag(), request.chatData().chatName(), instant));
        }catch (SQLException e){
            logger.error("ChatDAO error", e);
        }
        return Optional.empty();
    }
    public Optional<ChatDTO> selectChatById(UUID uuid){
        try(Connection connection = this.dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(SELECT_BY_CHAT_ID)){
            stmt.setObject(1, uuid);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()) return Optional.ofNullable(chat(rs));
            }
        }catch (SQLException e){
            logger.error("ChatDAO error", e);
        }catch (Exception e){
            logger.error("ChatDAO error", e);
        }
        return Optional.empty();
    }
    private ChatDTO chat(ResultSet rs) throws Exception{
        return new ChatDTO(rs.getObject("chat_id", UUID.class), ChatType.nameToChatType(rs.getString("chat_type")), rs.getString("chat_tag"), rs.getString("chat_name"), rs.getTimestamp("created_at").toInstant());
    }
    private ChatDTO chat(UUID chatId, ChatType chatType, String chatTag, String chatName, Instant instant){
        return new ChatDTO(chatId, chatType, chatTag, chatName, instant);
    }
}
