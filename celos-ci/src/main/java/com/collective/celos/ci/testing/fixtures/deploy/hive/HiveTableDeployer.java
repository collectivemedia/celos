package com.collective.celos.ci.testing.fixtures.deploy.hive;

import au.com.bytecode.opencsv.CSVWriter;
import com.collective.celos.Util;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.fixtures.deploy.CelosCiDirtyStateException;
import com.collective.celos.ci.testing.fixtures.deploy.FixtureDeployer;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by akonopko on 16.01.15.
 */
public class HiveTableDeployer implements FixtureDeployer {

    private static final String CREATE_TEMP_TABLE_PATTERN = "CREATE TABLE %s.%s_temp(%s) ROW FORMAT DELIMITED FIELDS TERMINATED BY '\\t' STORED AS TEXTFILE";
    private static final String PARTITION_PATTERN = "PARTITION(%s)";
    private static final String DESCRIBE_TABLE_PATTERN = "DESCRIBE %s.%s";
    private static final String CREATE_DB_PATTERN = "CREATE DATABASE IF NOT EXISTS %s";
    private static final String USE_DB_PATTERN = "USE %s";
    private static final String DROP_DB_PATTERN = "DROP DATABASE IF EXISTS %s CASCADE";
    private static final String LOAD_TMP_DATA_PATTERN = "LOAD DATA INPATH '%s' OVERWRITE INTO TABLE %s.%s_temp";
    private static final String LOAD_DATA_PATTERN = "INSERT INTO TABLE %s %s SELECT * FROM %s_temp";
    private static final String SANDBOX_PARAM = "${SANDBOX}";
    private static final String PARTITION_INFORMATION_MARKER = "# Partition Information";
    private static final String SET_PARTITION_MODE_NONSTRICT = "set hive.exec.dynamic.partition.mode=nonstrict;";
    private static final String SHOW_DATABASES = "SHOW DATABASES LIKE '%s'";

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

    @Override
    public void deploy(TestRun testRun) throws Exception {

        try(Connection connection = getConnection(testRun); Statement statement = connection.createStatement()) {

            String mockedDbName = Util.augmentDbName(testRun.getTestUUID(), databaseName);

            System.out.println("Test case " + testRun.getTestCase().getName() + ": Deploying " + tableName + " to " + mockedDbName);

            createMockedDatabase(statement, mockedDbName);
            createMockedTable(statement, mockedDbName, testRun);

            if (dataFileCreator != null) {
                FixTable fixTable = dataFileCreator.create(testRun);
                Path tempHdfsFile = createTempHdfsFileForInsertion(fixTable, testRun);

                loadDataToMockedTable(statement, mockedDbName, tempHdfsFile, tableName);
            }
        }
    }

    @Override
    public void undeploy(TestRun testRun) throws Exception {

        try (Connection connection = getConnection(testRun); Statement statement = connection.createStatement()) {
            String mockedDbName = Util.augmentDbName(testRun.getTestUUID(), databaseName);
            String createMockedDb = String.format(DROP_DB_PATTERN, mockedDbName);
            statement.execute(createMockedDb);
        }
    }

    @Override
    public void validate(TestRun testRun) throws Exception {
        try (Connection connection = getConnection(testRun); Statement statement = connection.createStatement()) {
            String mockedDbName = Util.augmentDbName(testRun.getTestUUID(), databaseName);
            String createMockedDb = String.format(SHOW_DATABASES, mockedDbName);
            ResultSet rs = statement.executeQuery(createMockedDb);
            if (rs.next()) {
                throw new CelosCiDirtyStateException("Celos-CI temporaty Hive DB still exists: " + mockedDbName);
            }
        }

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

    private Path createTempHdfsFileForInsertion(FixTable fixTable, TestRun testRun) throws Exception {

        Path pathToParent = new Path(testRun.getHdfsPrefix(), ".hive");
        Path pathTo = new Path(pathToParent, UUID.randomUUID().toString());
        FileSystem fileSystem = testRun.getCiContext().getFileSystem();
        fileSystem.mkdirs(pathTo.getParent());
        FSDataOutputStream outputStream = fileSystem.create(pathTo);

        CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream), '\t', CSVWriter.NO_QUOTE_CHARACTER);

        for (FixTable.FixRow fixRow : fixTable.getRows()) {
            List<String> rowData = Lists.newArrayList();
            for (String colName : fixTable.getColumnNames()) {
                rowData.add(fixRow.getCells().get(colName));
            }
            String[] dataArray = rowData.toArray(new String[rowData.size()]);
            writer.writeNext(dataArray);
        }

        writer.close();

        fileSystem.setPermission(pathToParent, new FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL));
        fileSystem.setPermission(pathTo, new FsPermission(FsAction.ALL, FsAction.ALL, FsAction.ALL));
        return pathTo;
    }

    private void loadDataToMockedTable(Statement statement, String mockedDatabase, Path dataFile, String tableName) throws SQLException, IOException {

        List<String> columnDef = Lists.newArrayList();
        List<String> partDef = Lists.newArrayList();
        parseTableDefinition(columnDef, partDef, mockedDatabase, statement);

        String createMockedTbl = String.format(CREATE_TEMP_TABLE_PATTERN, mockedDatabase, tableName, StringUtils.join(columnDef, ",\n"));
        statement.executeUpdate(createMockedTbl);

        String loadDataTmp = String.format(LOAD_TMP_DATA_PATTERN, dataFile.toString(), mockedDatabase, tableName);
        statement.executeUpdate(loadDataTmp);

        loadFromTempToRealDb(statement, tableName, partDef);
    }

    private void loadFromTempToRealDb(Statement statement, String tableName, List<String> partDef) throws SQLException {
        String partitions;
        if (partDef.isEmpty()) {
            partitions = "";
        } else {
            partitions = String.format(PARTITION_PATTERN, StringUtils.join(partDef, ", "));
        }

        statement.execute(SET_PARTITION_MODE_NONSTRICT);
        String loadData = String.format(LOAD_DATA_PATTERN, tableName, partitions, tableName);
        statement.executeUpdate(loadData);
    }

    void parseTableDefinition(List<String> columns, List<String> partColumns, String mockedDatabase, Statement statement) throws SQLException {

        String describeQuery = String.format(DESCRIBE_TABLE_PATTERN, mockedDatabase, tableName);
        ResultSet res = statement.executeQuery(describeQuery);

        boolean partitionInfo = false;
        while (res.next()) {
            String column = res.getString(1);
            String type = res.getString(2);
            if (PARTITION_INFORMATION_MARKER.equals(column)) {
                partitionInfo = true;
            } else {
                if (column != null && type != null && !column.startsWith("#")) {
                    if (partitionInfo) {
                        partColumns.add(column.trim());
                    } else {
                        columns.add(column.trim() + " " + type.trim());
                    }
                }
            }
        }
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

    public FixObjectCreator<FixFile> getTableCreationScriptFile() {
        return tableCreationScriptFile;
    }
}
