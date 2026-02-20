package com.lfj.messenger.server.dao;

import com.lfj.dev.annotations.ThreadSafe;
import com.lfj.messenger.bcrypt.PasswordUtil;
import com.lfj.messenger.dto.datatype.server.UserDTO;
import com.lfj.messenger.dto.request.AuthRequest;
import com.lfj.messenger.dto.request.RegisterRequest;
import com.lfj.messenger.time.Time;
import com.lfj.messenger.uuid7.UUIDv7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.Instant;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class UserDAO {
    private DataSource source;
    private Logger logger;
    private static final String INSERT = "INSERT INTO users_table(user_id, email, display_name, user_name, password, created_at) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_USER_EMAIL = "SELECT user_id, display_name, user_name, password, created_at FROM users_table WHERE email = ?";
    private static final String SELECT_ALL_USER = "SELECT user_id, display_name, user_name, created_at FROM users_table";
    private static final String SELECT_BY_USER_ID = "SELECT user_id, display_name, user_name, created_at FROM users_table WHERE user_id = ?";
    private UserDAO(){ this.logger = LoggerFactory.getLogger(UserDAO.class); }
    public UserDAO(DataSource source) {
        this();
        this.source = source;
    }
    @ThreadSafe
    public Supplier<Optional<UserDTO>> authorizationAsync(AuthRequest request){ return () -> authorizationUser(request); }
    @ThreadSafe
    public Supplier<Optional<UserDTO>> registerAsync(RegisterRequest request){
        return () -> registerUser(request);
    }
    @ThreadSafe
    public Supplier<Set<UserDTO>> userList(Set<UUID> ids){ return () -> users(ids); }

    @ThreadSafe
    public Optional<UserDTO> registerUser(RegisterRequest request){
        logger.info("Register users...");
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)){
            UUID uuid = UUIDv7.next();
            Instant instant = Time.nowInstant();
            stmt.setObject(1, uuid);
            stmt.setString(2, request.email());
            stmt.setString(3, request.displayName());
            stmt.setString(4, null);
            stmt.setString(5, PasswordUtil.hashPassword(request.password()));
            stmt.setObject(6, Timestamp.from(instant));
            stmt.executeUpdate();
            return Optional.of(new UserDTO(uuid, request.displayName(), null, instant));
        } catch (SQLException e) {
            logger.error("{} - {}. State - {}", e.getMessage(), e.getErrorCode(), e.getSQLState());
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public Optional<UserDTO> authorizationUser(AuthRequest request) {
        logger.info("authorization user...");
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(SELECT_BY_USER_EMAIL)){
            stmt.setString(1, request.email());
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    if(PasswordUtil.validPassword(request.password(), rs.getString("password"))) return Optional.ofNullable(mapToUserDTO(rs));
                }
            }
            logger.warn("User not found!");
            return Optional.empty();
        }catch (SQLException e){
            logger.error("{} - {}. State: {}", e.getMessage(), e.getErrorCode(), e.getSQLState(), e);
            return Optional.empty();
        }
    }
    public Optional<UserDTO> findUserById(UUID uuid){
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(SELECT_BY_USER_ID)){
            stmt.setObject(1, uuid);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()) return Optional.of(mapToUserDTO(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error("{} - {}. State {}", e.getMessage(), e.getErrorCode(), e.getSQLState());
            return Optional.empty();
        }
    }
    public Set<UserDTO> users(Set<UUID> ids){
        Set<UserDTO> list = new HashSet<>();
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(SELECT_BY_USER_ID)) {
            for(UUID id : ids){
                stmt.setObject(1, id);
                try(ResultSet rs = stmt.executeQuery()){ if(rs.next()) list.add(mapToUserDTO(rs)); }
            }
        }catch (SQLException e){
            logger.error("{} - {}. {}", e.getMessage(), e.getErrorCode(), e.getSQLState());
            logger.error("Error", e);
        }
        return list;
    }
    private UserDTO mapToUserDTO(ResultSet rs) throws SQLException{
        return new UserDTO(
                rs.getObject("user_id", UUID.class),
                rs.getString("display_name"),
                rs.getString("user_name"),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}