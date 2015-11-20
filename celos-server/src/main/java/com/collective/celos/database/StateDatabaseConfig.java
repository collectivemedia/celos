package com.collective.celos.database;

/**
 * Created by akonopko on 20.11.15.
 */
public interface StateDatabaseConfig {

    public static enum DatabaseType {
        FILESYSTEM, JDBC
    }

    public DatabaseType getDatabaseType();

}
