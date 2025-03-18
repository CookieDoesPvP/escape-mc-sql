package com.escapemc.mysql;

import org.bukkit.plugin.java.JavaPlugin;

public class Register extends JavaPlugin {
    private static Register instance;

    public static Register getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // ✅ Start HikariCP instead of old MySQL
        DatabaseManager.initialize(SQLInfo.getHost(), SQLInfo.getUser(), SQLInfo.getPassword(), SQLInfo.getDatabase(), SQLInfo.getPort());
    }

    @Override
    public void onDisable() {
    	DatabaseManager.close(); // ✅ Close HikariCP pool
    }
}
