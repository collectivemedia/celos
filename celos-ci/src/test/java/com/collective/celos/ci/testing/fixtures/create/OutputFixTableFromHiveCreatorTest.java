package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.DatabaseName;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.junit.Test;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by akonopko on 24.01.15.
 */
public class OutputFixTableFromHiveCreatorTest {

    @Test
    public void testCreateFixTable() throws SQLException {
        OutputFixTableFromHiveCreator creator = new OutputFixTableFromHiveCreator(new DatabaseName("db"), "table");

        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        TestRun testRun = mock(TestRun.class);
        ResultSet resultSet = mock(ResultSet.class);

        doReturn(UUID.nameUUIDFromBytes("123-234".getBytes())).when(testRun).getTestUUID();
        doReturn(statement).when(connection).createStatement();

        String expectedQuery = "SELECT * FROM celosci_db_b8d5f366_fa8f_3997_94c0_6fe3346df766.table";
        doReturn(resultSet).when(statement).executeQuery(expectedQuery);

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        doReturn(metaData).when(resultSet).getMetaData();

        doReturn(3).when(metaData).getColumnCount();
        doReturn("col1").when(metaData).getColumnName(1);
        doReturn("col2").when(metaData).getColumnName(2);
        doReturn("col3").when(metaData).getColumnName(3);

        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString("col1")).thenReturn("col1val1").thenReturn("col1val2").thenReturn("col1val3");
        when(resultSet.getString("col2")).thenReturn("col2val1").thenReturn("col2val2").thenReturn("col2val3");
        when(resultSet.getString("col3")).thenReturn("col3val1").thenReturn("col3val2").thenReturn("col3val3");

        FixTable resultTable = creator.createFixTable(testRun, statement);
        List<FixTable.FixRow> rowList = resultTable.getRows();

        Assert.assertEquals(rowList.size(), 2);
        FixTable.FixRow row1 = rowList.get(0);
        FixTable.FixRow row2 = rowList.get(1);

        Map<String,String> rowData1 = Maps.newHashMap();
        rowData1.put("col1", "col1val1");
        rowData1.put("col2", "col2val1");
        rowData1.put("col3", "col3val1");

        Map<String,String> rowData2 = Maps.newHashMap();
        rowData2.put("col1", "col1val2");
        rowData2.put("col2", "col2val2");
        rowData2.put("col3", "col3val2");

        Assert.assertEquals(rowData1, row1.getCells());
        Assert.assertEquals(rowData2, row2.getCells());
    }

    @Test
    public void testCreateFixTableNoData() throws SQLException {
        OutputFixTableFromHiveCreator creator = new OutputFixTableFromHiveCreator(new DatabaseName("db"), "table");

        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        TestRun testRun = mock(TestRun.class);
        ResultSet resultSet = mock(ResultSet.class);

        doReturn(UUID.nameUUIDFromBytes("123-234".getBytes())).when(testRun).getTestUUID();
        doReturn(statement).when(connection).createStatement();

        String expectedQuery = "SELECT * FROM celosci_db_b8d5f366_fa8f_3997_94c0_6fe3346df766.table";
        doReturn(resultSet).when(statement).executeQuery(expectedQuery);

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        doReturn(metaData).when(resultSet).getMetaData();

        doReturn(3).when(metaData).getColumnCount();
        doReturn("col1").when(metaData).getColumnName(1);
        doReturn("col2").when(metaData).getColumnName(2);
        doReturn("col3").when(metaData).getColumnName(3);

        when(resultSet.next()).thenReturn(false);

        FixTable resultTable = creator.createFixTable(testRun, statement);
        List<FixTable.FixRow> rowList = resultTable.getRows();

        Assert.assertEquals(rowList.size(), 0);
    }


}
