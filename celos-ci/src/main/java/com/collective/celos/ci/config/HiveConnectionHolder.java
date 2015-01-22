package com.collective.celos.ci.config;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by akonopko on 16.01.15.
 */
public class HiveConnectionHolder implements Closeable {

    private final String jdbcConnectionUrl;
    private final boolean driverLoaded;
    private Connection connection;

    public HiveConnectionHolder(String jdbcConnectionUrl) {
        this.jdbcConnectionUrl = jdbcConnectionUrl;
        this.driverLoaded = tryLoadDriverClass();
    }

    private boolean tryLoadDriverClass() {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public Statement createStatement() throws SQLException {
        validateDriverLoaded();
        if (connection == null) {
            connection = DriverManager.getConnection(jdbcConnectionUrl);
        }
        return connection.createStatement();
    }

    @Override
    public void close() throws IOException {
        validateDriverLoaded();
        try {
            connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    private void validateDriverLoaded() {
        if (!driverLoaded) {
            throw new IllegalStateException("Hive driver was not loaded");
        }
    }

}
