package com.lfj.messenger.servertest;

import com.lfj.messenger.server.service.DataBase;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseTest {
    private static final String usersTable = """
            CREATE TABLE IF NOT EXISTS users_table(
                user_id UUID PRIMARY KEY,
                user_name VARCHAR(50) UNIQUE,
                display_name VARCHAR(100) NOT NULL,
                email VARCHAR(255) NOT NULL UNIQUE,
                password TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL
            );
            """.stripIndent();
    private static final String chatsTable = """
            CREATE TABLE IF NOT EXISTS chats_table(
                chat_id UUID PRIMARY KEY,
                chat_type VARCHAR(15) NOT NULL,
                chat_tag VARCHAR(50) UNIQUE,
                chat_name VARCHAR(100),
                created_at TIMESTAMP NOT NULL
            );
            """.stripIndent();
    private static final String messagesTable = """
            CREATE TABLE IF NOT EXISTS messages_table(
                message_id UUID PRIMARY KEY,
                chat_id UUID NOT NULL REFERENCES chats_table(chat_id) ON DELETE CASCADE,
                sender_id UUID NOT NULL REFERENCES users_table(user_id) ON DELETE CASCADE,
                message_type VARCHAR(10) NOT NULL,
                content TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL
            );
            """.stripIndent();
    private static final String chatMembersTable = """
            CREATE TABLE IF NOT EXISTS chat_members_table(
                chat_id UUID NOT NULL REFERENCES chats_table (chat_id) ON DELETE CASCADE,
                user_id UUID NOT NULL REFERENCES users_table (user_id) ON DELETE CASCADE,
                role VARCHAR(20) NOT NULL,
                joined_at TIMESTAMP NOT NULL
            );
            """.stripIndent();
    public static DataSource getDataSource(){
        DataSource dataSource = DataBase.createDataSource("jdbc:h2:mem:testdb;MODE=PostgreSQL", "test", "");
        initSchema(dataSource);
        return dataSource;
    }
    private static void initSchema(DataSource dataSource){
        try(Connection connection = dataSource.getConnection(); Statement stmt = connection.createStatement()){
            stmt.execute(usersTable);
            stmt.execute(chatsTable);
            stmt.execute(messagesTable);
            stmt.execute(chatMembersTable);
        }catch (Exception e){
            throw new RuntimeException("Init schema error", e);
        }
    }
}
