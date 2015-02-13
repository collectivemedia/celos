package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 13.02.15.
 */
public class FixTableComparerTest {

    @Test
    public void testSuccess() throws Exception {
        FixObjectCreator<FixTable> expectedDataCreator = mock(FixObjectCreator.class);
        FixObjectCreator<FixTable> actualDataCreator = mock(FixObjectCreator.class);
        boolean columnNamesOrdered = true;
        FixTableComparer comparer = new FixTableComparer(expectedDataCreator, actualDataCreator, columnNamesOrdered, true);

        FixTable table1 = generateFixTable();
        FixTable table2 = generateFixTable();

        doReturn(table1).when(expectedDataCreator).create(null);
        doReturn(table2).when(actualDataCreator).create(null);

        FixObjectCompareResult result = comparer.check(null);
        Assert.assertEquals(FixObjectCompareResult.Status.SUCCESS, result.getStatus());
    }

    @Test
    public void testSuccessDiffRowOrder() throws Exception {
        FixObjectCreator<FixTable> expectedDataCreator = mock(FixObjectCreator.class);
        FixObjectCreator<FixTable> actualDataCreator = mock(FixObjectCreator.class);
        boolean columnNamesOrdered = true;
        FixTableComparer comparer = new FixTableComparer(expectedDataCreator, actualDataCreator, columnNamesOrdered, false);

        FixTable table1 = generateFixTable();
        FixTable table2 = generateFixTableDiffRowOrder();

        doReturn(table1).when(expectedDataCreator).create(null);
        doReturn(table2).when(actualDataCreator).create(null);

        FixObjectCompareResult result = comparer.check(null);
        Assert.assertEquals(FixObjectCompareResult.Status.SUCCESS, result.getStatus());
    }

    @Test
    public void testFailsDiffRowOrder() throws Exception {
        FixObjectCreator<FixTable> expectedDataCreator = mock(FixObjectCreator.class);
        FixObjectCreator<FixTable> actualDataCreator = mock(FixObjectCreator.class);
        boolean columnNamesOrdered = true;
        FixTableComparer comparer = new FixTableComparer(expectedDataCreator, actualDataCreator, columnNamesOrdered, false);

        FixTable table1 = generateFixTable();
        FixTable table2 = generateFixTableDiffData();

        doReturn(table1).when(expectedDataCreator).create(null);
        doReturn(table2).when(actualDataCreator).create(null);

        doReturn("table1").when(expectedDataCreator).getDescription(null);
        doReturn("table2").when(actualDataCreator).getDescription(null);

        String message = "Diff:\n" +
                "Actual [table2]:\n" +
                "FixTable.FixRow[cells={col1=val11_, col2=val22_}] [1 times]\n" +
                "FixTable.FixRow[cells={col1=val1_, col2=val2_}] [1 times]\n" +
                "Expected [table1]:\n" +
                "FixTable.FixRow[cells={col1=val1, col2=val2}] [1 times]\n" +
                "FixTable.FixRow[cells={col1=val11, col2=val22}] [1 times]\n";

        FixObjectCompareResult result = comparer.check(null);
        Assert.assertEquals(FixObjectCompareResult.Status.FAIL, result.getStatus());
        Assert.assertEquals(message, result.generateDescription());
    }

    @Test
    public void testFailDifferentCells() throws Exception {
        FixObjectCreator<FixTable> expectedDataCreator = mock(FixObjectCreator.class);
        FixObjectCreator<FixTable> actualDataCreator = mock(FixObjectCreator.class);
        boolean columnNamesOrdered = true;
        FixTableComparer comparer = new FixTableComparer(expectedDataCreator, actualDataCreator, columnNamesOrdered, true);

        FixTable table1 = generateFixTable();
        FixTable table2 = generateFixTableDiffData();

        doReturn(table1).when(expectedDataCreator).create(null);
        doReturn(table2).when(actualDataCreator).create(null);

        FixObjectCompareResult result = comparer.check(null);
        Assert.assertEquals(FixObjectCompareResult.Status.FAIL, result.getStatus());
        String expected = "Row #0 : Cells in expected data set: [col1=val1, col2=val2], cells in actual data set: [col1=val1_, col2=val2_]\n" +
                "Row #1 : Cells in expected data set: [col1=val11, col2=val22], cells in actual data set: [col1=val11_, col2=val22_]\n";

        Assert.assertEquals(expected, result.generateDescription());
    }

    @Test
    public void testFailDifferentCells2() throws Exception {
        FixObjectCreator<FixTable> expectedDataCreator = mock(FixObjectCreator.class);
        FixObjectCreator<FixTable> actualDataCreator = mock(FixObjectCreator.class);
        boolean columnNamesOrdered = true;
        FixTableComparer comparer = new FixTableComparer(expectedDataCreator, actualDataCreator, columnNamesOrdered, true);

        FixTable table1 = generateFixTable();
        FixTable table2 = generateFixTableDiffData();

        doReturn(table1).when(expectedDataCreator).create(null);
        doReturn(table2).when(actualDataCreator).create(null);

        FixObjectCompareResult result = comparer.check(null);
        Assert.assertEquals(FixObjectCompareResult.Status.FAIL, result.getStatus());
        String expected = "Row #0 : Cells in expected data set: [col1=val1, col2=val2], cells in actual data set: [col1=val1_, col2=val2_]\n" +
                "Row #1 : Cells in expected data set: [col1=val11, col2=val22], cells in actual data set: [col1=val11_, col2=val22_]\n";

        Assert.assertEquals(expected, result.generateDescription());
    }


    @Test
    public void testFailsColumnOrder() throws Exception {
        FixObjectCreator<FixTable> expectedDataCreator = mock(FixObjectCreator.class);
        FixObjectCreator<FixTable> actualDataCreator = mock(FixObjectCreator.class);
        boolean columnNamesOrdered = true;
        FixTableComparer comparer = new FixTableComparer(expectedDataCreator, actualDataCreator, columnNamesOrdered, true);

        FixTable table1 = generateFixTable();
        FixTable table2 = generateFixTableDifferentOrder();

        doReturn(table1).when(expectedDataCreator).create(null);
        doReturn(table2).when(actualDataCreator).create(null);

        FixObjectCompareResult result = comparer.check(null);
        Assert.assertEquals(FixObjectCompareResult.Status.FAIL, result.getStatus());
        Assert.assertEquals("Column Names for expected and actual data set differed: expected [col1, col2] but was [col2, col1]\n", result.generateDescription());
    }

    @Test
    public void testSuccessIgnoreColumnOrder() throws Exception {
        FixObjectCreator<FixTable> expectedDataCreator = mock(FixObjectCreator.class);
        FixObjectCreator<FixTable> actualDataCreator = mock(FixObjectCreator.class);
        boolean columnNamesOrdered = false;
        FixTableComparer comparer = new FixTableComparer(expectedDataCreator, actualDataCreator, columnNamesOrdered, true);

        FixTable table1 = generateFixTable();
        FixTable table2 = generateFixTableDifferentOrder();

        doReturn(table1).when(expectedDataCreator).create(null);
        doReturn(table2).when(actualDataCreator).create(null);

        FixObjectCompareResult result = comparer.check(null);
        Assert.assertEquals(FixObjectCompareResult.Status.SUCCESS, result.getStatus());
    }


    @Test
    public void testFailsColumnNumber() throws Exception {
        FixObjectCreator<FixTable> expectedDataCreator = mock(FixObjectCreator.class);
        FixObjectCreator<FixTable> actualDataCreator = mock(FixObjectCreator.class);
        boolean columnNamesOrdered = false;
        FixTableComparer comparer = new FixTableComparer(expectedDataCreator, actualDataCreator, columnNamesOrdered, true);

        FixTable table1 = generateFixTable();
        FixTable table2 = generateFixTableMoreColumns();

        doReturn(table1).when(expectedDataCreator).create(null);
        doReturn(table2).when(actualDataCreator).create(null);

        FixObjectCompareResult result = comparer.check(null);
        Assert.assertEquals(FixObjectCompareResult.Status.FAIL, result.getStatus());
        Assert.assertEquals("Column Names for expected and actual data set differed: expected [col1, col2] but was [col1, col2, col3]\n", result.generateDescription());
    }

    @Test
    public void testFailsRowNumber() throws Exception {
        FixObjectCreator<FixTable> expectedDataCreator = mock(FixObjectCreator.class);
        FixObjectCreator<FixTable> actualDataCreator = mock(FixObjectCreator.class);
        boolean columnNamesOrdered = false;
        FixTableComparer comparer = new FixTableComparer(expectedDataCreator, actualDataCreator, columnNamesOrdered, true);

        FixTable table1 = generateFixTable();
        FixTable table2 = generateFixTableMoreRows();

        doReturn(table1).when(expectedDataCreator).create(null);
        doReturn(table2).when(actualDataCreator).create(null);

        FixObjectCompareResult result = comparer.check(null);
        Assert.assertEquals(FixObjectCompareResult.Status.FAIL, result.getStatus());
        Assert.assertEquals("Data set size for expected and actual data set differed: expected has 2 while actual data has 3 rows\n", result.generateDescription());
    }

    private FixTable generateFixTable() {
        List<FixTable.FixRow> fixRows = Lists.newArrayList();
        Map<String, String> map = Maps.newHashMap();
        map.put("col1", "val1");
        map.put("col2", "val2");

        Map<String, String> map2 = Maps.newHashMap();
        map2.put("col1", "val11");
        map2.put("col2", "val22");

        FixTable.FixRow row1 = new FixTable.FixRow(map);
        FixTable.FixRow row2 = new FixTable.FixRow(map2);
        fixRows.add(row1);
        fixRows.add(row2);
        return new FixTable(Lists.newArrayList("col1", "col2"), fixRows);
    }

    private FixTable generateFixTableDiffRowOrder() {
        List<FixTable.FixRow> fixRows = Lists.newArrayList();
        Map<String, String> map = Maps.newHashMap();
        map.put("col1", "val1");
        map.put("col2", "val2");

        Map<String, String> map2 = Maps.newHashMap();
        map2.put("col1", "val11");
        map2.put("col2", "val22");

        FixTable.FixRow row1 = new FixTable.FixRow(map2);
        FixTable.FixRow row2 = new FixTable.FixRow(map);
        fixRows.add(row1);
        fixRows.add(row2);
        return new FixTable(Lists.newArrayList("col1", "col2"), fixRows);
    }

    private FixTable generateFixTableDiffData() {
        List<FixTable.FixRow> fixRows = Lists.newArrayList();
        Map<String, String> map = Maps.newHashMap();
        map.put("col1", "val1_");
        map.put("col2", "val2_");

        Map<String, String> map2 = Maps.newHashMap();
        map2.put("col1", "val11_");
        map2.put("col2", "val22_");

        FixTable.FixRow row1 = new FixTable.FixRow(map);
        FixTable.FixRow row2 = new FixTable.FixRow(map2);
        fixRows.add(row1);
        fixRows.add(row2);
        return new FixTable(Lists.newArrayList("col1", "col2"), fixRows);
    }

    private FixTable generateFixTableMoreRows() {
        List<FixTable.FixRow> fixRows = Lists.newArrayList();
        Map<String, String> map = Maps.newHashMap();
        map.put("col1", "val1");
        map.put("col2", "val2");

        Map<String, String> map2 = Maps.newHashMap();
        map2.put("col1", "val21");
        map2.put("col2", "val22");

        Map<String, String> map3 = Maps.newHashMap();
        map3.put("col1", "val31");
        map3.put("col2", "val32");

        FixTable.FixRow row1 = new FixTable.FixRow(map);
        FixTable.FixRow row2 = new FixTable.FixRow(map2);
        FixTable.FixRow row3 = new FixTable.FixRow(map3);
        fixRows.add(row1);
        fixRows.add(row2);
        fixRows.add(row3);

        return new FixTable(Lists.newArrayList("col1", "col2"), fixRows);
    }

    private FixTable generateFixTableMoreColumns() {
        List<FixTable.FixRow> fixRows = Lists.newArrayList();
        Map<String, String> map = Maps.newHashMap();
        map.put("col1", "val1");
        map.put("col2", "val2");
        map.put("col3", "val3");

        Map<String, String> map2 = Maps.newHashMap();
        map2.put("col1", "val11");
        map2.put("col2", "val22");
        map2.put("col3", "val22");

        FixTable.FixRow row1 = new FixTable.FixRow(map);
        FixTable.FixRow row2 = new FixTable.FixRow(map2);
        fixRows.add(row1);
        fixRows.add(row2);
        return new FixTable(Lists.newArrayList("col1", "col2", "col3"), fixRows);
    }

    private FixTable generateFixTableDifferentOrder() {
        List<FixTable.FixRow> fixRows = Lists.newArrayList();
        Map<String, String> map = Maps.newHashMap();
        map.put("col1", "val1");
        map.put("col2", "val2");

        Map<String, String> map2 = Maps.newHashMap();
        map2.put("col1", "val11");
        map2.put("col2", "val22");

        FixTable.FixRow row1 = new FixTable.FixRow(map);
        FixTable.FixRow row2 = new FixTable.FixRow(map2);
        fixRows.add(row1);
        fixRows.add(row2);
        return new FixTable(Lists.newArrayList("col2", "col1"), fixRows);
    }

}
