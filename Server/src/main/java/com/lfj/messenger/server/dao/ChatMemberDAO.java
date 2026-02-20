package com.lfj.messenger.server.dao;

import com.lfj.messenger.dto.datatype.server.ChatMemberDTO;
import com.lfj.messenger.dto.datatype.server.UserDTO;
import com.lfj.messenger.dto.types.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChatMemberDAO {
    private DataSource dataSource;
    private Logger logger;

    private final static String INSERT = "INSERT INTO chat_members_table (chat_id, user_id, role, joined_at) VALUES (?, ?, ?, ?)";
    private final static String SELECT_BY_CHAT_ID = "SELECT user_id, role, joined_at FROM chat_members_table WHERE chat_id = ?";
    private final static String SELECT_USER_ID_BY_CHAT_ID = "SELECT user_id FROM chat_members_table WHERE chat_id = ?";

    private ChatMemberDAO(){  }
    public ChatMemberDAO(DataSource dataSource){
        this.logger = LoggerFactory.getLogger(ChatMemberDAO.class);
        this.dataSource = dataSource;
    }

    public void addMemberForPrivateChat(UUID chatId, Set<UUID> members){
        try(Connection connection = this.dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT)){
            for(UUID member : members){
                Instant instant = Instant.now();
                stmt.setObject(1, chatId);
                stmt.setObject(2, member);
                stmt.setString(3, Role.MEMBER.name());
                stmt.setTimestamp(4, Timestamp.from(instant));
                stmt.executeUpdate();
            }
        }catch (SQLException e){
            logger.error("Database error", e);
        }
    }

    public Optional<ChatMemberDTO> selectChatMembersById(UUID chatId, Set<UserDTO> users){
        Map<Role, Set<ChatMemberDTO.Member>> members = new HashMap<>();
        Map<UUID, UserDTO> map = users.stream().collect(Collectors.toMap(UserDTO::userId, Function.identity()));
        try(Connection connection = this.dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(SELECT_BY_CHAT_ID)) {
            stmt.setObject(1, chatId);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    Role role = Role.valueOf(rs.getString("role"));
                    UUID userId = rs.getObject("user_id", UUID.class);
                    UserDTO user = map.get(userId);
                    Instant instant = rs.getTimestamp("joined_at").toInstant();
                    members.computeIfAbsent(role, v -> new HashSet<>()).add(new ChatMemberDTO.Member(user, instant));
                }
            }
            return Optional.of(new ChatMemberDTO(chatId, members));
        }catch (SQLException e){
            logger.error("Database error", e);
        }
        return Optional.empty();
    }
    public Optional<Set<UUID>> getUsersId(UUID chatId){
        try(Connection connection = this.dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(SELECT_USER_ID_BY_CHAT_ID)){
            Set<UUID> userIds = new HashSet<>();
            stmt.setObject(1, chatId);
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()){
                    userIds.add(rs.getObject("user_id", UUID.class));
                }
            }
            return Optional.of(userIds);
        } catch (SQLException e) {
            logger.error("Database error", e);
        }
        return Optional.empty();
    }
}