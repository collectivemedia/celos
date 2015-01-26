package com.collective.celos.ci.testing.fixtures.deploy.hive;

import com.collective.celos.Util;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.deploy.FixtureDeployer;
import com.google.common.collect.Lists;

import java.sql.*;
import java.util.List;

/**
 * Created by akonopko on 16.01.15.
 */
public class HiveTableDeployer implements FixtureDeployer {

    private static final String CREATE_TABLE_PATTERN = "CREATE TABLE %s.%s LIKE %s.%s";
    private static final String CREATE_DB_PATTERN = "CREATE DATABASE IF NOT EXISTS %s";
    private static final String DROP_DB_PATTERN = "DROP DATABASE %s CASCADE";

    private static final boolean driverLoaded = tryLoadDriverClass();

    private final String databaseName;
    private final String tableName;
    private Connection connection;

    public HiveTableDeployer(String databaseName, String tableName) throws Exception {
        if (!driverLoaded) {
            throw new IllegalStateException("Hive JDBC driver was not found");
        }
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    private static boolean tryLoadDriverClass() {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void deploy(TestRun testRun) throws Exception {

        this.connection = DriverManager.getConnection(testRun.getCiContext().getTarget().getHiveJdbc().toString());
        Statement statement = connection.createStatement();

        String mockedDbName = Util.augmentDbName(testRun.getTestUUID(), databaseName);
        createMockedDatabase(statement, mockedDbName, databaseName, tableName);

        statement.close();
    }

    @Override
    public void undeploy(TestRun testRun) throws Exception {
        Statement statement = connection.createStatement();
        String mockedDbName = Util.augmentDbName(testRun.getTestUUID(), databaseName);
        dropMockedDatabase(statement, mockedDbName);
        statement.close();
        connection.close();
    }

    private void dropMockedDatabase(Statement statement, String mockedDatabase) throws SQLException {
        String createMockedDb = String.format(DROP_DB_PATTERN, mockedDatabase);
        statement.execute(createMockedDb);
    }

    private void createMockedDatabase(Statement statement, String mockedDatabase, String databaseName, String tableName) throws SQLException {
        String createMockedDb = String.format(CREATE_DB_PATTERN, mockedDatabase);
        statement.execute(createMockedDb);

        String createTable = String.format(CREATE_TABLE_PATTERN, mockedDatabase, tableName, databaseName, tableName);
        statement.execute(createTable);
    }

}
