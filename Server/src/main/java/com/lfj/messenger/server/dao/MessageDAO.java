package com.lfj.messenger.server.dao;

import com.lfj.dev.annotations.ThreadSafe;
import com.lfj.messenger.dto.datatype.server.MessageDTO;
import com.lfj.messenger.dto.datatype.server.UserDTO;
import com.lfj.messenger.dto.request.GetMessageRequest;
import com.lfj.messenger.dto.request.MessageRequest;
import com.lfj.messenger.dto.request.GetMessageRequest.Direction;
import com.lfj.messenger.dto.types.MessageType;
import com.lfj.messenger.uuid7.UUIDv7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Supplier;

public class MessageDAO {
    private DataSource source;
    private Logger logger;
    private MessageDAO(){ this.logger = LoggerFactory.getLogger(MessageDAO.class); }

    private static final String WRITE_MESSAGE_TO_BD = "INSERT INTO messages_table(message_id, chat_id, sender_id, message_type, content, created_at) VALUES(?, ?, ?, ?, ?, ?)";
    private static final String READ_LATEST_MESSAGES = "SELECT message_id, chat_id, sender_id, message_type, content, created_at FROM messages_table WHERE chat_id = ? ORDER BY message_id DESC LIMIT ?";
    private static final String READ_OLDER_MESSAGES = "SELECT message_id, chat_id, sender_id, message_type, content, created_at FROM messages_table WHERE chat_id = ? AND message_id < ? ORDER BY message_id DESC LIMIT ?";
    private static final String GET_SENDER_ID_BY_CHAT_ID = "SELECT sender_id FROM messages_table WHERE chat_id = ? LIMIT ?";

    public MessageDAO(DataSource source){
        this();
        this.source = source;
    }
    @ThreadSafe
    public Supplier<Optional<MessageDTO>> writeAndSendMessageAsync(MessageRequest request){
        return () -> writeAndSendMessage(request);
    }
    @ThreadSafe
    public Supplier<Optional<List<MessageDTO>>> readNewerMessageAsync(GetMessageRequest request, Set<UserDTO> users){
        return () -> readNewerMessage(request, users);
    }
    @ThreadSafe
    public Supplier<Optional<List<MessageDTO>>> readOlderMessageAsync(GetMessageRequest request, Set<UserDTO> users){
        return () -> readOlderMessage(request, users);
    }
    @ThreadSafe
    public Supplier<Optional<List<MessageDTO>>> readMessageAsync(GetMessageRequest request, Set<UserDTO> users){
        if(request.direction() == Direction.NEWER) return () -> readNewerMessage(request, users);
        else return () -> readOlderMessage(request, users);
    }
    public Optional<MessageDTO> writeAndSendMessage(MessageRequest request){
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(WRITE_MESSAGE_TO_BD)) {
            UUID messageId = UUIDv7.next();
            Instant instant = Instant.now();
            stmt.setObject(1, messageId);
            stmt.setObject(2, request.getChatId());
            stmt.setObject(3, request.getSender().userId());
            stmt.setString(4, request.message().messageType().name());
            stmt.setString(5, request.message().content());
            stmt.setTimestamp(6, Timestamp.from(instant));
            stmt.executeUpdate();
            return Optional.ofNullable(message(messageId, request.getChatId(), request.getSender(), request.message().messageType(), request.message().content(), instant));
        } catch (SQLException e) {
            logger.error("{} - {}. State: {}", e.getMessage(), e.getErrorCode(), e.getSQLState());
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public Optional<Set<UUID>> getSenderId(UUID chatId, int limit){
        Set<UUID> senderIds = new HashSet<>();
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(GET_SENDER_ID_BY_CHAT_ID)){
            stmt.setObject(1, chatId);
            stmt.setInt(2, limit);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()){
                    UUID id = rs.getObject("sender_id", UUID.class);
                    if(!senderIds.contains(id)) senderIds.add(id);
                }
            }
        }catch (SQLException e){
            logger.error("Database error", e);
        }
        return Optional.of(senderIds);
    }

    public Optional<List<MessageDTO>> readNewerMessage(GetMessageRequest request, Set<UserDTO> users){
        if(request.direction() != GetMessageRequest.Direction.NEWER) return Optional.empty();

        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(READ_LATEST_MESSAGES)){            List<MessageDTO> messages = new ArrayList<>();
            stmt.setObject(1, request.chatId());
            stmt.setInt(2, request.limit());
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    UUID userId = rs.getObject("sender_id", UUID.class);
                    Optional<UserDTO> user = users.stream().filter(f -> f.userId().equals(userId)).findFirst();
                    if(user.isPresent()) messages.add(message(rs, user.get()));
                }
            }
            Collections.reverse(messages);
            return Optional.of(messages);
        }catch (SQLException e){
            logger.error("MessageDAO error", e);
        }
        return Optional.empty();
    }

    public Optional<List<MessageDTO>> readOlderMessage(GetMessageRequest request, Set<UserDTO> users){
        if(request.direction() != GetMessageRequest.Direction.OLDER) return Optional.empty();
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(READ_OLDER_MESSAGES)){
            stmt.setObject(1, request.chatId());
            stmt.setObject(2, request.cursorMessageId());
            stmt.setInt(3, request.limit());
            List<MessageDTO> messages = new ArrayList<>();
            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()){
                    UUID userId = rs.getObject("sender_id", UUID.class);
                    Optional<UserDTO> user = users.stream().filter(f -> f.userId().equals(userId)).findFirst();
                    if(user.isPresent()) messages.add(message(rs, user.get()));
                }
            }
            Collections.reverse(messages);
            return Optional.of(messages);
        }catch (SQLException e){
            logger.error("MessageDAO error", e);
        }
        return Optional.empty();
    }

    private MessageDTO message(ResultSet rs, UserDTO user) throws SQLException {
        return new MessageDTO(rs.getObject("message_id", UUID.class), rs.getObject("chat_id", UUID.class), user, MessageType.valueOf(rs.getString("message_type")), rs.getString("content"), rs.getTimestamp("created_at").toInstant());
    }

    private MessageDTO message(UUID messageId, UUID chatId, UserDTO user, MessageType messageType, String content, Instant instant){
        return new MessageDTO(messageId, chatId, user, messageType, content, instant);
    }
}