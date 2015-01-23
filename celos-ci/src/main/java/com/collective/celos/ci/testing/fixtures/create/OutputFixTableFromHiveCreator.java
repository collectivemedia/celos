package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.Util;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 22.01.15.
 */
public class OutputFixTableFromHiveCreator implements FixObjectCreator<FixTable> {

    private final static String READ_TABLE_DATA = "SELECT * FROM %s.%s";
    public static final String DESCRIPTION = "Hive table %s.%s";

    private final String databaseName;
    private final String tableName;

    public OutputFixTableFromHiveCreator(String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    @Override
    public FixTable create(TestRun testRun) throws Exception {

        Connection connection = DriverManager.getConnection(testRun.getCiContext().getTarget().getHiveJdbc().toString());
        try {
            String augumentedDbData = Util.augumentDbName(testRun.getTestUUID(), databaseName);
            String query = String.format(READ_TABLE_DATA, augumentedDbData, tableName);
            ResultSet rs = connection.createStatement().executeQuery(query);

            Set<String> columnNames = Sets.newHashSet();
            for (int i=0; i < rs.getMetaData().getColumnCount(); i++) {
                columnNames.add(rs.getMetaData().getColumnName(i + 1));
            }

            List<FixTable.FixRow> fixRows = Lists.newArrayList();
            while (rs.next()) {
                Map<String, String> rowData = new HashMap<>();
                for (String colName : columnNames) {
                    rowData.put(colName, rs.getString(colName));
                }
                fixRows.add(new FixTable.FixRow(rowData));
            }

            return new FixTable(fixRows);
        } finally {
            connection.close();
        }
    }

    @Override
    public String getDescription(TestRun testRun) {
        return String.format(DESCRIPTION, Util.augumentDbName(testRun.getTestUUID(), databaseName), tableName);
    }
}
