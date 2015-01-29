package com.collective.celos.ci.testing.fixtures.deploy.hive;

import au.com.bytecode.opencsv.CSVWriter;
import com.collective.celos.Util;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.fixtures.deploy.FixtureDeployer;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileStore;
import java.sql.*;
import java.util.List;

/**
 * Created by akonopko on 16.01.15.
 */
public class HiveTableDeployer implements FixtureDeployer {

    private static final String CREATE_TEMP_TABLE_PATTERN = "CREATE TABLE %s.%s_temp(%s) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' STORED AS TEXTFILE";
    private static final String DESCRIBE_TABLE_PATTERN = "DESCRIBE %s.%s";
    private static final String CREATE_DB_PATTERN = "CREATE DATABASE IF NOT EXISTS %s";
    private static final String USE_DB_PATTERN = "USE %s";
    private static final String DROP_DB_PATTERN = "DROP DATABASE %s CASCADE";
    private static final String LOAD_TMP_DATA_PATTERN = "LOAD DATA LOCAL INPATH '%s' OVERWRITE INTO TABLE %s.%s_temp";
    private static final String LOAD_DATA_PATTERN = "INSERT INTO TABLE %s.%s SELECT * FROM %s.%s_temp";
    private static final String SANDBOX_PARAM = "${SANDBOX}";
    private static final boolean driverLoaded = tryLoadDriverClass();

    private final String databaseName;
    private final String tableName;
    private final FixObjectCreator<FixTable> dataFileCreator;
    private final FixObjectCreator<FixFile> tableCreationScriptFile;

    public HiveTableDeployer(String databaseName, String tableName, FixObjectCreator<FixFile> tableCreationScriptFile, FixObjectCreator<FixTable> dataFileCreator) throws Exception {
        if (!driverLoaded) {
            throw new IllegalStateException("Hive JDBC driver was not found");
        }
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.dataFileCreator = dataFileCreator;
        this.tableCreationScriptFile = tableCreationScriptFile;
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


    private Connection getConnection(TestRun testRun) throws SQLException {
        return DriverManager.getConnection(testRun.getCiContext().getTarget().getHiveJdbc().toString());
    }

    @Override
    public void deploy(TestRun testRun) throws Exception {

        try (Connection connection = getConnection(testRun)) {
            Statement statement = connection.createStatement();

            String mockedDbName = Util.augmentDbName(testRun.getTestUUID(), databaseName);

            createMockedDatabase(statement, mockedDbName);
            System.out.println("Test case " + testRun.getTestCase().getName() + ": Created Hive DB " + mockedDbName);

            createMockedTable(statement, mockedDbName, testRun);

            if (dataFileCreator != null) {
                FixTable fixTable = dataFileCreator.create(testRun);
                File tempFile = prepareTempFileForInsertion(fixTable, testRun);
                loadDataToMockedTable(statement, mockedDbName, tempFile, tableName);
            }

            statement.close();
        }
    }

    public File prepareTempFileForInsertion(FixTable fixTable, TestRun testRun) throws Exception {
        File hiveLoadFile = new File(testRun.getCelosTempDir(), databaseName + "." + tableName);

        CSVWriter writer = new CSVWriter(new FileWriter(hiveLoadFile), '\t');

        for (FixTable.FixRow fixRow : fixTable.getRows()) {
            List<String> rowData = Lists.newArrayList();
            for (String colName : fixTable.getColumnNames()) {
                rowData.add(fixRow.getCells().get(colName));
            }
            String[] dataArray = rowData.toArray(new String[rowData.size()]);
            writer.writeNext(dataArray);
        }

        writer.close();
        return hiveLoadFile;
    }

    @Override
    public void undeploy(TestRun testRun) throws Exception {

        try (Connection connection = getConnection(testRun)) {
            Statement statement = connection.createStatement();

            String mockedDbName = Util.augmentDbName(testRun.getTestUUID(), databaseName);
            dropMockedDatabase(statement, mockedDbName);

            System.out.println("Test case " + testRun.getTestCase().getName() + ": Dropped Hive DB " + mockedDbName);

            statement.close();
        }
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

    private void dropMockedDatabase(Statement statement, String mockedDatabase) throws SQLException {
        String createMockedDb = String.format(DROP_DB_PATTERN, mockedDatabase);
        statement.execute(createMockedDb);
    }

    private void createMockedDatabase(Statement statement, String mockedDatabase) throws Exception {

        String createMockedDb = String.format(CREATE_DB_PATTERN, mockedDatabase);
        statement.execute(createMockedDb);
    }

    private void createMockedTable(Statement statement, String mockedDatabase, TestRun testRun) throws Exception {
        statement.execute(String.format(USE_DB_PATTERN, mockedDatabase));
        FixFile scriptFile = tableCreationScriptFile.create(testRun);
        String creationScript = IOUtils.toString(scriptFile.getContent());
        creationScript = creationScript.replace(SANDBOX_PARAM, testRun.getCiContext().getFileSystem().getUri() + testRun.getHdfsPrefix());
        statement.execute(creationScript);
    }

    public FixObjectCreator<FixTable> getDataFileCreator() {
        return dataFileCreator;
    }

}
