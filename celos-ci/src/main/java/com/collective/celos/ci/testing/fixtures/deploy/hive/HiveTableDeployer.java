package com.collective.celos.ci.testing.fixtures.deploy.hive;

import com.collective.celos.Util;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.deploy.FixtureDeployer;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 16.01.15.
 */
public class HiveTableDeployer implements FixtureDeployer {

    private static final String CREATE_TABLE_PATTERN = "CREATE TABLE %s.%s LIKE %s.%s";
    private static final String CREATE_TEMP_TABLE_PATTERN = "CREATE TABLE %s.%s_temp(%s) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' STORED AS TEXTFILE";
    private static final String DESCRIBE_TABLE_PATTERN = "DESCRIBE %s.%s";
    private static final String CREATE_DB_PATTERN = "CREATE DATABASE IF NOT EXISTS %s";
    private static final String DROP_DB_PATTERN = "DROP DATABASE %s CASCADE";
    private static final String LOAD_TMP_DATA_PATTERN = "LOAD DATA LOCAL INPATH '%s' OVERWRITE INTO TABLE %s.%s_temp";
    private static final String LOAD_DATA_PATTERN = "INSERT INTO TABLE %s.%s SELECT * FROM %s.%s_temp";

    private static final boolean driverLoaded = tryLoadDriverClass();

    private final String databaseName;
    private final String tableName;
    private final HiveFileCreator dataFileCreator;
    private Connection connection;

    public HiveTableDeployer(String databaseName, String tableName, HiveFileCreator dataFileCreator) throws Exception {
        if (!driverLoaded) {
            throw new IllegalStateException("Hive JDBC driver was not found");
        }
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.dataFileCreator = dataFileCreator;
    }

    private static boolean tryLoadDriverClass() {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public HiveFileCreator getDataFileCreator() {
        return dataFileCreator;
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

        String mockedDbName = Util.augumentDbName(testRun.getTestUUID(), databaseName);
        createMockedDatabase(statement, mockedDbName, databaseName, tableName);

        if (dataFileCreator != null) {
            loadDataToMockedTable(statement, mockedDbName, dataFileCreator.create(testRun), tableName);
        }

        statement.close();
    }

    @Override
    public void undeploy(TestRun testRun) throws Exception {
        Statement statement = connection.createStatement();
        String mockedDbName = Util.augumentDbName(testRun.getTestUUID(), databaseName);
        dropMockedDatabase(statement, mockedDbName);
        statement.close();
        connection.close();
    }

    private void loadDataToMockedTable(Statement statement, String mockedDatabase, File dataFile, String tableName) throws SQLException {

        String describeQuery = String.format(DESCRIBE_TABLE_PATTERN, databaseName, tableName);
        ResultSet res = statement.executeQuery(describeQuery);
        List<String> tableColumns = getColumnDefinitionLines(res);
        String createMockedTbl = String.format(CREATE_TEMP_TABLE_PATTERN, mockedDatabase, tableName, StringUtils.join(tableColumns, ",\n"));
        statement.execute(createMockedTbl);

        String loadDataTmp = String.format(LOAD_TMP_DATA_PATTERN, dataFile.getAbsolutePath(), mockedDatabase, tableName);
        statement.execute(loadDataTmp);

        String loadData = String.format(LOAD_DATA_PATTERN, mockedDatabase, tableName, mockedDatabase, tableName);
        statement.execute(loadData);
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

    private List<String> getColumnDefinitionLines(ResultSet res) throws SQLException {
        List<String> tableColumns = Lists.newArrayList();
        while (res.next()) {
            String column = res.getString(1);
            String type = res.getString(2);
            if (column != null && type != null && !column.startsWith("#")) {
                tableColumns.add(column.trim() + " " + type.trim());
            }
        }
        return tableColumns;
    }

}
