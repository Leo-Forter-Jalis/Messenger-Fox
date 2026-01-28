package com.lfj.messenger.server.dao;

import com.lfj.messenger.dto.request.GetMessageRequest;
import com.lfj.messenger.dto.request.MessageRequest;
import com.lfj.messenger.dto.response.MessageResponse;
import com.lfj.messenger.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Supplier;

public class MessageDAO {
    private DataSource source;
    private Logger logger;
    private MessageDAO(){ this.logger = LoggerFactory.getLogger(MessageDAO.class); }

    private static final String WRITE_MESSAGE_TO_BD = "INSERT INTO messages_table(message_id, sender_id, receiver_id, content) VALUES(?, ?, ?, ?) ON CONFLICT (message_id) DO NOTHING";
    private static final String READ_MESSAGES = "SELECT message_id, sender_id, receiver_id, content, create_date FROM messages_table WHERE sender_id=?";

    public MessageDAO(DataSource source){
        this();
        this.source = source;
    }

    public Supplier<Optional<MessageResponse>> writeAndSendMessageAsync(MessageRequest request){
        return () -> writeAndSendMessage(request);
    }

    public Optional<MessageResponse> writeAndSendMessage(MessageRequest request){
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(WRITE_MESSAGE_TO_BD)) {
            stmt.setObject(1, request.message().messageId());
            stmt.setObject(2, request.message().senderId()); // На char ID ты положил болт
            stmt.setObject(3, request.message().receiverId());
            stmt.setString(4, request.message().content());
            stmt.executeUpdate();
            return Optional.ofNullable(request.convertToResponse());
        } catch (SQLException e) {
            logger.error("{} - {}. State: {}", e.getMessage(), e.getErrorCode(), e.getSQLState());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // Чтение сообщений пока не-не. Класс GetMessageRequest

}
