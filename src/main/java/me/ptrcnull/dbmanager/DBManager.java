package me.ptrcnull.dbmanager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.config.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;

public abstract class DBManager {
    private HikariConfig hikariConfig;
    private HikariDataSource dataSource;
    private Connection conn;

    public DBManager(Configuration config) {
        this.hikariConfig = buildHikariConfig(
            config.getString("username", ""),
            config.getString("password", ""),
            config.getString("hostname", "localhost"),
            config.getString("port", "3306"),
            config.getString("database", "")
        );
    }

    public DBManager(ConfigurationSection config) {
        this.hikariConfig = buildHikariConfig(
            config.getString("username", ""),
            config.getString("password", ""),
            config.getString("hostname", "localhost"),
            config.getString("port", "3306"),
            config.getString("database", "")
        );
    }

    public Connection getConn() {
        return conn;
    }

    public PreparedStatement prepare(String statement) throws SQLException {
        return getConn().prepareStatement(statement);
    }

    private HikariConfig buildHikariConfig(String username, String password, String hostname, String port, String database) {
        HikariConfig dbConfig = new HikariConfig();

        dbConfig.setDriverClassName("com.mysql.jdbc.Driver");
        dbConfig.setUsername(username);
        dbConfig.setUsername(password);
        dbConfig.setConnectionTimeout(30 * 1_000L);
        dbConfig.setMaxLifetime(30 * 1_000L);

        dbConfig.setJdbcUrl(String.format(
            "jdbc:mysql://%s:%s/%s",
            hostname,
            port,
            database
        ));

        return dbConfig;
    }

    public boolean setup() {
        dataSource = new HikariDataSource(hikariConfig);
        try {
            conn = dataSource.getConnection();
            postSetup();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public abstract void postSetup() throws SQLException;

    public void close() {
        dataSource.close();
    }
}
