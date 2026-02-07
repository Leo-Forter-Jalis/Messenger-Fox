package com.lfj.messenger.base.db;

import com.lfj.dev.annotations.ActiveDevelopment;
import com.lfj.dev.annotations.Priority;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@ActiveDevelopment(priority = Priority.MEDIUM)
public class Database {
    private HikariDataSource dataSource;
    private Logger logger;
    private final String BEGIN_URL = "jdbc:sqlite:db/";
    private Database(){  }
    public Database(UUID id){
        File file = new File("db", id.toString());
        if(!file.exists()) file.mkdirs();
        this.logger = LoggerFactory.getLogger(Database.class);
        String url = BEGIN_URL + id + "/database.db";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(3000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(18000);
        config.setLeakDetectionThreshold(10000);
        this.dataSource = new HikariDataSource(config);
        try {
            createDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public DataSource getDataSource(){
        return dataSource;
    }

    private void createDB() throws SQLException {
        String createDB = """
                    CREATE TABLE IF NOT EXISTS users (
                        id TEXT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        user_name VARCHAR(100) UNIQUE,
                        create_at INTEGER NOT NULL
                    )
                    """.stripIndent();
        try (PreparedStatement statement = dataSource.getConnection().prepareStatement(createDB)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Database error", e);
            throw new SQLException(e);
        }
    }

}
