package com.lfj.messfox.server.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public final class DataBase {

    private DataBase(){  }

     public static class PostgresDB{
        public static DataSource dataSource(){
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/messfox_db");
            config.setUsername("postgres");
            config.setPassword("LolryPI");

            initialize(config);
            return new HikariDataSource(config);
        }
    }

     public static class H2TestDB{
        public static DataSource dataSource(){
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:h2:mem:test_data_base");
            config.setUsername("USER_TEST");
            config.setPassword("");

            initialize(config);
            DataSource dataSource = new HikariDataSource(config);
            initializeDataBase(dataSource);
            return dataSource;
        }
         private static void initializeDataBase(DataSource dataSource){
             String usersTable = """
            CREATE TABLE IF NOT EXISTS users_table(
                user_id UUID PRIMARY KEY,
                user_name VARCHAR(50) UNIQUE,
                display_name VARCHAR(100) NOT NULL,
                email VARCHAR(255) NOT NULL UNIQUE,
                password TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL
            );
            """.stripIndent();
             String chatsTable = """
            CREATE TABLE IF NOT EXISTS chats_table(
                chat_id UUID PRIMARY KEY,
                chat_type VARCHAR(15) NOT NULL,
                chat_tag VARCHAR(50) UNIQUE,
                chat_name VARCHAR(100),
                created_at TIMESTAMP NOT NULL
            );
            """.stripIndent();
             String messagesTable = """
            CREATE TABLE IF NOT EXISTS messages_table(
                message_id UUID PRIMARY KEY,
                chat_id UUID NOT NULL REFERENCES chats_table(chat_id) ON DELETE CASCADE,
                sender_id UUID NOT NULL REFERENCES users_table(user_id) ON DELETE CASCADE,
                message_type VARCHAR(10) NOT NULL,
                content TEXT NOT NULL,
                created_at TIMESTAMP NOT NULL
            );
            """.stripIndent();
             String chatMembersTable = """
            CREATE TABLE IF NOT EXISTS chat_members_table(
                chat_id UUID NOT NULL REFERENCES chats_table (chat_id) ON DELETE CASCADE,
                user_id UUID NOT NULL REFERENCES users_table (user_id) ON DELETE CASCADE,
                role VARCHAR(20) NOT NULL,
                joined_at TIMESTAMP NOT NULL
            );
            """.stripIndent();
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
    private static void initialize(HikariConfig config){
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(180000);
        config.setLeakDetectionThreshold(10000);
    }
}