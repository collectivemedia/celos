package com.collective.celos.database;

/**
 * Created by akonopko on 19.11.15.
 */
public class StateDatabase {

    private final StateDatabaseConfig config;

    public StateDatabase(StateDatabaseConfig config) {
        this.config = config;
    }

    public StateDatabaseConnection openConnection() throws Exception {
        switch (config.getDatabaseType()) {
            case JDBC:
                return new JDBCStateDatabaseConnection((JDBCStateDatabaseConnection.Config) config);
            case FILESYSTEM:
                return new FileSystemStateDatabaseConnection((FileSystemStateDatabaseConnection.Config) config);
            default:
                throw new IllegalArgumentException("Unsupported Celos DB type: " + config.getDatabaseType());
        }
    }
}
