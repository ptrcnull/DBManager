package me.ptrcnull.dbmanager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        dbConfig.setMaximumPoolSize(32);
        dbConfig.setMinimumIdle(8);

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

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private PreparedStatement prepare(Connection conn, String sql, Object... args) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 1; i <= args.length; i++) {
            Object arg = args[i];
            if (arg instanceof String) {
                stmt.setString(i, (String) arg);
            } else if (arg instanceof Integer) {
                stmt.setInt(i, (Integer) arg);
            } else if (arg instanceof Boolean) {
                stmt.setBoolean(i, (Boolean) arg);
            } else if (arg instanceof BigDecimal) {
                stmt.setBigDecimal(i, (BigDecimal) arg);
            } else if (arg instanceof Double) {
                stmt.setDouble(i, (Double) arg);
            } else if (arg instanceof Float) {
                stmt.setFloat(i, (Float) arg);
            } else if (arg instanceof Long) {
                stmt.setLong(i, (Long) arg);
            } else if (arg instanceof Short) {
                stmt.setShort(i, (Short) arg);
            } else {
                stmt.setObject(i, arg);
            }
        }
        return stmt;
    }

    public ResultSet query(String sql, Object... args) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = prepare(conn, sql, args);

        ResultSet result = stmt.executeQuery();

        stmt.close();
        conn.close();
        return result;
    }

    public void update(String sql, Object... args) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = prepare(conn, sql, args);

        stmt.executeUpdate();

        stmt.close();
        conn.close();
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
