package me.ptrcnull.database;

import org.bukkit.configuration.ConfigurationSection;

public abstract class BukkitDatabase extends Database {
    public BukkitDatabase(ConfigurationSection config) {
        super(
            config.getString("username", ""),
            config.getString("password", ""),
            config.getString("hostname", "localhost"),
            config.getString("port", "3306"),
            config.getString("database", "")
        );
    }
}
