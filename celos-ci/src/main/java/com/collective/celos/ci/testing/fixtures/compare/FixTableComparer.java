package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by akonopko on 13.02.15.
 */
public class FixTableComparer implements FixtureComparer {

    private final FixObjectCreator<FixTable> expectedDataCreator;
    private final FixObjectCreator<FixTable> actualDataCreator;
    private final boolean columnNamesOrdered;
    private final boolean respectRowOrder;

    public FixTableComparer(FixObjectCreator<FixTable> expectedDataCreator, FixObjectCreator<FixTable> actualDataCreator, boolean columnNamesOrdered, boolean respectRowOrder) {
        this.expectedDataCreator = expectedDataCreator;
        this.actualDataCreator = actualDataCreator;
        this.columnNamesOrdered = columnNamesOrdered;
        this.respectRowOrder = respectRowOrder;
    }

    public FixObjectCreator<FixTable> getExpectedDataCreator() {
        return expectedDataCreator;
    }

    public FixObjectCreator<FixTable> getActualDataCreator() {
        return actualDataCreator;
    }

    public boolean isColumnNamesOrdered() {
        return columnNamesOrdered;
    }

    public boolean isRespectRowOrder() {
        return respectRowOrder;
    }

    @Override
    public FixObjectCompareResult check(TestRun testRun) throws Exception {
        FixTable expected = expectedDataCreator.create(testRun);
        FixTable actual = actualDataCreator.create(testRun);

        List<String> expectedColumns = expected.getColumnNames();
        List<String> actualColumns = actual.getColumnNames();
        boolean wrongColNames = columnNamesOrdered && !expectedColumns.equals(actualColumns);
        wrongColNames |= !columnNamesOrdered && !equalsIgnoreOrder(expectedColumns, actualColumns);

        if (wrongColNames) {
            String expectedColDesc = StringUtils.join(expectedColumns, ", ");
            String actualColDesc = StringUtils.join(actualColumns, ", ");
            return FixObjectCompareResult.failed(
                    "Column Names for expected and actual data set differed: expected [" +
                    expectedColDesc + "] but was [" + actualColDesc + "]"
            );
        }

        if (expected.getRows().size() != actual.getRows().size()) {
            return FixObjectCompareResult.failed(
                    "Data set size for expected and actual data set differed: expected has " +
                            expected.getRows().size() + " while actual data has " + actual.getRows().size() + " rows"
            );
        }

        if (respectRowOrder) {
            Map<String, FixObjectCompareResult> fails = compareRespectOrder(expected, actual);
            if (!fails.isEmpty()) {
                return FixObjectCompareResult.wrapFailed(fails, "");
            }
            return FixObjectCompareResult.SUCCESS;
        } else {
            Map<FixTable.FixRow, Integer> expectedRes = countEntries(expected);
            Map<FixTable.FixRow, Integer> actualRes = countEntries(actual);

            return CompareHelper.compareEntityNumber(testRun, actualDataCreator, expectedDataCreator, expectedRes, actualRes);
        }

    }

    private Map<FixTable.FixRow, Integer> countEntries(FixTable expected) {
        Map<FixTable.FixRow, Integer> expEntryCount = Maps.newHashMap();

        for (FixTable.FixRow row : expected.getRows()) {
            Integer count = expEntryCount.get(row);
            if (count == null) {
                count = 1;
            } else {
                count = count + 1;
            }
            expEntryCount.put(row, count);
        }
        return expEntryCount;
    }

    private Map<String, FixObjectCompareResult> compareRespectOrder(FixTable expected, FixTable actual) {
        Map<String, FixObjectCompareResult> fails = Maps.newLinkedHashMap();
        ListIterator<FixTable.FixRow> expIter = expected.getRows().listIterator();
        ListIterator<FixTable.FixRow> actIter = actual.getRows().listIterator();
        while (expIter.hasNext()) {
            FixTable.FixRow expRow = expIter.next();
            FixTable.FixRow actRow = actIter.next();

            FixObjectCompareResult result = compareFixRows(expected.getColumnNames(), expRow, actRow);
            if (result.getStatus() == FixObjectCompareResult.Status.FAIL) {
                fails.put("Row #" + expIter.previousIndex(), result);
            }
        }
        return fails;
    }

    private FixObjectCompareResult compareFixRows(List<String> columnNames, FixTable.FixRow expRow, FixTable.FixRow actRow) {
        if (expRow.equals(actRow)) {
            return FixObjectCompareResult.SUCCESS;
        }

        List<String> expDesc = Lists.newArrayList();
        List<String> actDesc = Lists.newArrayList();

        for (String key: columnNames) {
            String expValue = expRow.getCells().get(key);
            String actValue = actRow.getCells().get(key);
            if (!expValue.equals(actValue)) {
                expDesc.add(key + "=" + expValue);
                actDesc.add(key + "=" + actValue);
            }
        }

        String differenctDescription =
                "Cells in expected data set: [" + StringUtils.join(expDesc, ", ") + "], " +
                "cells in actual data set: [" + StringUtils.join(actDesc, ", ") + "]";

        return FixObjectCompareResult.failed(differenctDescription);
    }

    private boolean equalsIgnoreOrder(Collection col1, Collection col2) {
        return col1.containsAll(col2) && col2.containsAll(col1);
    }

}
