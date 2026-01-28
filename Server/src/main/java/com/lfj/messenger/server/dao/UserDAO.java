package com.lfj.messenger.server.dao;

import com.lfj.messenger.bcrypt.PasswordUtil;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.request.AuthRequest;
import com.lfj.messenger.dto.request.ChatsRequest;
import com.lfj.messenger.dto.request.RegisterRequest;
import com.lfj.messenger.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class UserDAO {
    private DataSource source;
    private Logger logger;

    private static final String INSERT = "INSERT INTO users_table(user_id, email, name, user_name, password, create_date) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT (email) DO NOTHING";
    private static final String SELECT_BY_USER_EMAIL = "SELECT user_id, email, name, user_name, password, create_date FROM users_table WHERE email = ?";
    private static final String SELECT_ALL_USER = "SELECT user_id, email, name, user_name, create_date FROM users_table";
    private static final String SELECT_BY_USER_ID = "SELECT user_id, email, name, user_name, create_date FROM users_table WHERE user_id = ?";

    private UserDAO(){ this.logger = LoggerFactory.getLogger(UserDAO.class); }

    public UserDAO(DataSource source) {
        this();
        this.source = source;
    }

    public Supplier<Optional<UserDTO>> authorizationAsync(AuthRequest request){
        return () -> authorizationUser(request);
    }

    public Supplier<Optional<UserDTO>> registerAsync(RegisterRequest request){
        return () -> registerUser(request);
    }

    public Supplier<List<UserDTO>> userList(ChatsRequest request){ return this::users; }

    public Optional<UserDTO> registerUser(RegisterRequest request){
        logger.info("Register users...");
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)){
            UUID uuid = UUID.randomUUID();
            Instant instant = Time.nowInstant();
            stmt.setObject(1, uuid);
            stmt.setString(2, request.email());
            stmt.setString(3, request.displayName());
            stmt.setString(4, request.userName());
            stmt.setString(5, request.password()); // Временное решение\
            stmt.setObject(6, Timestamp.from(instant));
            stmt.executeUpdate();
            return Optional.of(new UserDTO(uuid, request.displayName(), request.userName(), request.email(), instant));
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
                    if(request.password().equals(rs.getString("password")))
                    return Optional.ofNullable(mapToUserDTO(rs));
                }
            }
            logger.warn("User not found!");
            return Optional.empty();
        }catch (SQLException e){
            logger.error("{} - {}. State: {}", e.getMessage(), e.getErrorCode(), e.getSQLState());
            logger.error("Error", e);
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

    public List<UserDTO> users(){
        try(Connection connection = this.source.getConnection(); PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_USER)) {
            ResultSet rs = stmt.executeQuery();
            List<UserDTO> list = new ArrayList();
            while (rs.next()){
                list.add(mapToUserDTO(rs));
            }
            return list;
        }catch (SQLException e){
            logger.error("{} - {}. {}", e.getMessage(), e.getErrorCode(), e.getSQLState());
            logger.error("Error", e);
            return List.of();
        }
    }

    private UserDTO mapToUserDTO(ResultSet rs) throws SQLException{
        return new UserDTO(
                rs.getObject("user_id", UUID.class),
                rs.getString("name"),
                rs.getString("user_name"),
                rs.getString("email"),
                rs.getTimestamp("create_date").toInstant()
        );
    }

}
