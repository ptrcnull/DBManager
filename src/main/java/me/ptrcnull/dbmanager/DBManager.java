package me.ptrcnull.dbmanager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            int j = i + 1;
            if (arg instanceof String) {
                stmt.setString(j, (String) arg);
            } else if (arg instanceof Integer) {
                stmt.setInt(j, (Integer) arg);
            } else if (arg instanceof Boolean) {
                stmt.setBoolean(j, (Boolean) arg);
            } else if (arg instanceof BigDecimal) {
                stmt.setBigDecimal(j, (BigDecimal) arg);
            } else if (arg instanceof Double) {
                stmt.setDouble(j, (Double) arg);
            } else if (arg instanceof Float) {
                stmt.setFloat(j, (Float) arg);
            } else if (arg instanceof Long) {
                stmt.setLong(j, (Long) arg);
            } else if (arg instanceof Short) {
                stmt.setShort(j, (Short) arg);
            } else if (arg instanceof Timestamp) {
                stmt.setTimestamp(j, (Timestamp) arg);
            } else if (arg instanceof Instant) {
                stmt.setTimestamp(j, Timestamp.from((Instant) arg));
            } else {
                stmt.setObject(j, arg);
            }
        }
        return stmt;
    }

    public <T> List<T> query(RowParser<T> callback, String sql, Object... args) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = prepare(conn, sql, args);

        ResultSet result = stmt.executeQuery();
        List<T> rows = new ArrayList<>();
        while (result.next()) {
            rows.add(callback.parse(result));
        }

        stmt.close();
        conn.close();
        return rows;
    }

    public void update(String sql, Object... args) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = prepare(conn, sql, args);

        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }

    public boolean setup() {
        try {
            dataSource = new HikariDataSource(hikariConfig);
            postSetup();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public abstract void postSetup() throws SQLException;

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public interface RowParser<T> {
        T parse(ResultSet resultSet) throws SQLException;
    }
}
