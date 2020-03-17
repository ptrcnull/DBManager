package me.ptrcnull.dbmanager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public abstract class DBManager {
    private HikariConfig hikariConfig;
    private HikariDataSource dataSource;

    public DBManager(String username, String password, String hostname, String port, String database) {
        HikariConfig dbConfig = new HikariConfig();

        dbConfig.setDriverClassName("com.mysql.jdbc.Driver");
        dbConfig.setUsername(username);
        dbConfig.setPassword(password);
        dbConfig.setConnectionTimeout(30 * 1_000L);
        dbConfig.setMaxLifetime(30 * 1_000L);

        dbConfig.setJdbcUrl(String.format(
            "jdbc:mysql://%s:%s/%s",
            hostname,
            port,
            database
        ));

        this.hikariConfig = dbConfig;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public PreparedStatement prepare(String statement) throws SQLException {
        return dataSource.getConnection().prepareStatement(statement);
    }

    public boolean setup() {
        dataSource = new HikariDataSource(hikariConfig);
        try {
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
