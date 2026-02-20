package com.lfj.messenger.server.service;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataBase {
    public static DataSource createDataSource(String url, String name, String password){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(name);
        config.setPassword(password);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(3000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(180000);
        config.setLeakDetectionThreshold(10000);
        return new HikariDataSource(config);
    }
}