package com.ukmgenshin.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/** Connection pool (HikariCP) untuk seluruh aplikasi. */
public final class DBUtil {

    private static HikariDataSource dataSource;

    private DBUtil() {
    }

    private static synchronized HikariDataSource ds() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl(AppConfig.dbUrl());
            config.setUsername(AppConfig.dbUser());
            config.setPassword(AppConfig.dbPassword());
            config.setMaximumPoolSize(10);
            config.setPoolName("ukm-genshin-pool");
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return ds().getConnection();
    }

    public static synchronized void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
