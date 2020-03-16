package me.ptrcnull.dbmanager;

import net.md_5.bungee.config.Configuration;

public abstract class BungeeDBManager extends DBManager {
    public BungeeDBManager(Configuration config) {
        super(
            config.getString("username", ""),
            config.getString("password", ""),
            config.getString("hostname", "localhost"),
            config.getString("port", "3306"),
            config.getString("database", "")
        );
    }
}
