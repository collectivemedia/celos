package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
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

        return compareTables(expected, actual);
    }

    private FixObjectCompareResult compareTables(FixTable expected, FixTable actual) {
        FixTable expectedToCompare;
        FixTable actualToCompare;

        if (respectRowOrder) {
            expectedToCompare = expected;
            actualToCompare = actual;
        } else {
            expectedToCompare = orderFixTableRows(expected);
            actualToCompare = orderFixTableRows(actual);
        }

        Map<String, FixObjectCompareResult> fails = compareOrderedTable(expectedToCompare, actualToCompare);

        if (!fails.isEmpty()) {
            return FixObjectCompareResult.wrapFailed(fails, "");
        }

        return FixObjectCompareResult.SUCCESS;
    }

    private FixTable orderFixTableRows(FixTable expected) {
        List<FixTable.FixRow> expectedRows = Lists.newArrayList(expected.getRows());
        expectedRows.sort(fixRowComparator);
        return new FixTable(expected.getColumnNames(), expectedRows);
    }

    private Comparator<FixTable.FixRow> fixRowComparator = new Comparator<FixTable.FixRow>() {

        @Override
        public int compare(FixTable.FixRow o1, FixTable.FixRow o2) {
            return Integer.valueOf(o1.hashCode()).compareTo(o2.hashCode());
        }
    };

    private Map<String, FixObjectCompareResult> compareOrderedTable(FixTable expected, FixTable actual) {
        Map<String, FixObjectCompareResult> fails = Maps.newHashMap();
        ListIterator<FixTable.FixRow> expIter = expected.getRows().listIterator();
        ListIterator<FixTable.FixRow> actIter = actual.getRows().listIterator();
        while (expIter.hasNext()) {
            FixTable.FixRow expRow = expIter.next();
            FixTable.FixRow actRow = actIter.next();

            FixObjectCompareResult result = compareFixRows(expRow, actRow);
            if (result.getStatus() == FixObjectCompareResult.Status.FAIL) {
                fails.put("Row #" + expIter.previousIndex(), result);
            }
        }
        return fails;
    }

    private FixObjectCompareResult compareFixRows(FixTable.FixRow expRow, FixTable.FixRow actRow) {
        if (expRow.equals(actRow)) {
            return FixObjectCompareResult.SUCCESS;
        }

        Map<String, MapDifference.ValueDifference<String>> differing = Maps.difference(expRow.getCells(), actRow.getCells()).entriesDiffering();
        List<String> expDesc = Lists.newArrayList();
        List<String> actDesc = Lists.newArrayList();
        for (String key: differing.keySet()) {
            String expValue = expRow.getCells().get(key);
            String actValue = actRow.getCells().get(key);
            expDesc.add(key + "=" + expValue);
            actDesc.add(key + "=" + actValue);
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
