package com.escapemc.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private static HikariDataSource dataSource;

    /**
     * Initializes the HikariCP connection pool.
     */
    public static void initialize(String host, String user, String password, String database, String port) {
        if (dataSource != null) {
            Bukkit.getLogger().info("⚠ HikariCP is already initialized!");
            return;
        }

        try {
            HikariConfig config = new HikariConfig();

            // ✅ Database connection settings
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true");
            config.setUsername(user);
            config.setPassword(password);

            // ✅ Connection Pooling Settings
            config.setMaximumPoolSize(10); // Max active connections
            config.setMinimumIdle(2); // Min idle connections
            config.setIdleTimeout(30000); // Close idle connections after 30 seconds
            config.setMaxLifetime(600000); // Recreate connections every 10 minutes
            config.setConnectionTimeout(5000); // 5s timeout for getting a connection

            // ✅ Optimization for Spigot/Paper
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
            Bukkit.getLogger().info("✅ Successfully connected to MySQL using HikariCP.");

        } catch (Exception e) {
            Bukkit.getLogger().severe("❌ Failed to initialize HikariCP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection from the HikariCP pool.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("Database connection pool not initialized!");
        }
        return dataSource.getConnection();
    }

    /**
     * Closes the database pool when the plugin is disabled.
     */
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            Bukkit.getLogger().info("✅ HikariCP connection pool closed.");
        }
    }
}
