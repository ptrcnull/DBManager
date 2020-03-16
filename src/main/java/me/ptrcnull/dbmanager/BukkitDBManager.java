package me.ptrcnull.dbmanager;

import org.bukkit.configuration.ConfigurationSection;

public abstract class BukkitDBManager extends DBManager {
    public BukkitDBManager(ConfigurationSection config) {
        super(
            config.getString("username", ""),
            config.getString("password", ""),
            config.getString("hostname", "localhost"),
            config.getString("port", "3306"),
            config.getString("database", "")
        );
    }
}
