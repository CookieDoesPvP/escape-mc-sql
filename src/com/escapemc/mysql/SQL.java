package com.escapemc.mysql;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQL {

    /**
     * Checks if a column exists in a table.
     * Uses HikariCP for optimized performance.
     *
     * @param table  The table name.
     * @param column The column name.
     * @return True if the column exists, otherwise false.
     */
    public static boolean columnExists(String table, String column) {
        String query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
             
            ps.setString(1, table);
            ps.setString(2, column);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("❌ SQL Error in columnExists(): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Checks if a table exists in the database using HikariCP.
     *
     * @param table The name of the table to check.
     * @return true if the table exists, false otherwise.
     */
    public static boolean tableExists(String table) {
        String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, table);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("❌ SQL Error checking table existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Creates a table in the database synchronously if it does not already exist.
     *
     * @param table   The table name.
     * @param columns The table columns and their types (e.g., "id INT PRIMARY KEY, name VARCHAR(255)").
     */
    public static void createTable(String table, String columns) {
        if (!tableExists(table)) {
            String query = "CREATE TABLE " + table + " (" + columns + ")";
            Bukkit.getLogger().info("🚀 Creating table: " + table);
            Bukkit.getLogger().info("📜 SQL Query: " + query);

            try (Connection connection = DatabaseManager.getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                ps.executeUpdate();
                Bukkit.getLogger().info("✅ Table created: " + table);
            } catch (SQLException e) {
                Bukkit.getLogger().severe("❌ SQL Error creating table " + table + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Bukkit.getLogger().info("⚠ Table " + table + " already exists, skipping creation.");
        }
    }

    /**
     * Creates a table in the database asynchronously if it does not already exist.
     *
     * @param table   The table name.
     * @param columns The table columns and their types.
     */
    public static void createTableAsync(String table, String columns) {
        new BukkitRunnable() {
            @Override
            public void run() {
                createTable(table, columns);
            }
        }.runTaskAsynchronously(Register.getInstance());
    }

    /**
     * Executes a SQL UPDATE (INSERT, UPDATE, DELETE) synchronously.
     * This should be used for shutdown or critical operations.
     */
    public static void executeUpdate(String query, Object... params) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            setParams(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("❌ SQL Sync Update Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes a SQL UPDATE (INSERT, UPDATE, DELETE) asynchronously.
     * This should be used for player actions and non-critical updates.
     */
    public static void executeUpdateAsync(String query, Object... params) {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeUpdate(query, params);
            }
        }.runTaskAsynchronously(Register.getInstance());
    }

    /**
     * Executes a SELECT query synchronously and processes results with a callback.
     */
    public static void executeQuery(String query, QueryCallback callback, Object... params) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                callback.onQueryResult(rs);
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("❌ SQL Query Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes a SELECT query asynchronously and processes results with a callback.
     */
    public static void executeQueryAsync(String query, QueryCallback callback, Object... params) {
        new BukkitRunnable() {
            @Override
            public void run() {
                executeQuery(query, callback, params);
            }
        }.runTaskAsynchronously(Register.getInstance());
    }


    
    /**
     * Helper method to set parameters in PreparedStatement.
     */
    private static void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    /**
     * Callback interface for handling result sets.
     */
    @FunctionalInterface
    public interface QueryCallback {
        void onQueryResult(ResultSet rs) throws SQLException;
    }
}
