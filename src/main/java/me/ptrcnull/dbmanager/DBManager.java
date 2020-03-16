package me.ptrcnull.dbmanager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public abstract class DBManager {
    private HikariConfig hikariConfig;
    private HikariDataSource dataSource;
    private Connection conn;

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

    public Connection getConn() {
        return conn;
    }

    public PreparedStatement prepare(String statement) throws SQLException {
        return getConn().prepareStatement(statement);
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
