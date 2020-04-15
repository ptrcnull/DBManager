package me.ptrcnull.database;

import net.md_5.bungee.config.Configuration;

public abstract class BungeeDatabase extends Database {
    public BungeeDatabase(Configuration config) {
        super(
            config.getString("username", ""),
            config.getString("password", ""),
            config.getString("hostname", "localhost"),
            config.getString("port", "3306"),
            config.getString("database", "")
        );
    }
}
