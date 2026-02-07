package com.lfj.messenger.base.db;

import com.lfj.dev.annotations.ActiveDevelopment;
import com.lfj.dev.annotations.NotNull;
import com.lfj.dev.annotations.Priority;
import com.lfj.messenger.dto.datatype.UserDTO;
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

@ActiveDevelopment(priority = Priority.HIGH)
public class UserDB {
    private DataSource dataSource;
    private final String INSERT = "INSERT INTO users (id, name, user_name, create_at) VALUES (?, ?, ?, ?)";
    private final String SELECT = "SELECT id, name, user_name, create_at FROM users WHERE id = ?";
    private final String SELECT_ALL = "SELECT id, name, user_name, create_at FROM users";
    private final String DELETE = "DELETE FROM users WHERE id = ?";

    private Logger logger;
    public UserDB(DataSource dataSource){
        this.dataSource = dataSource;
        this.logger = LoggerFactory.getLogger(UserDB.class);
    }

    public CompletableFuture<Optional<UserDTO>> selectUserAsync(@NotNull UUID id){
        return CompletableFuture.supplyAsync(() -> selectUser(id));
    }
    public CompletableFuture<Optional<List<UserDTO>>> selectAllUserAsync(){
        return CompletableFuture.supplyAsync(this::selectAll);
    }
    public void insertUserAsync(@NotNull UserDTO user){
        CompletableFuture.runAsync(() -> insertUser(user));
    }
    public CompletableFuture<Void> deleteUserAsync(@NotNull UUID id){
        return CompletableFuture.runAsync(() -> deleteUser(id));
    }
    public void insertUser(UserDTO user){
        try(Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(INSERT)){
            stmt.setString(1 , user.userId().toString());
            stmt.setString(2, user.displayName());
            stmt.setString(3, user.userName());
            stmt.setLong(4, user.createAt().toEpochMilli());
            stmt.executeUpdate();
        }catch (SQLException e){
            logger.error("Database error", e);
        }
    }
    public Optional<UserDTO> selectUser(UUID id){
        try(Connection connection = this.dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(SELECT)){
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return Optional.of(user(rs));
            }
        }catch (SQLException e){
            logger.error("Database error", e);
        }
        return Optional.empty();
    }
    public Optional<List<UserDTO>> selectAll(){
        try(Connection connection = this.dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(SELECT_ALL); ResultSet rs = stmt.executeQuery()){
            List<UserDTO> list = new ArrayList<>();
            while (rs.next()){
                list.add(user(rs));
            }
            return Optional.of(list);
        }catch (SQLException e){
            logger.error("Database error", e);
        }
        return Optional.empty();
    }
    public void deleteUser(UUID id){
        try(Connection connection = this.dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement(DELETE)){
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        }catch (SQLException e){
            logger.error("Database error", e);
        }
    }
    private UserDTO user(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        String name = rs.getString("name");
        String userName = rs.getString("user_name");
        Instant instant = Instant.ofEpochMilli(rs.getLong("create_at"));

        return new UserDTO(id, name, userName, instant);
    }
}