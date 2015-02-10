package com.collective.celos.ci.testing.structure.fixobject;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FixTableTest {

    @Test
    public void testGetOrderedColumns() {
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
        FixTable fixTable = new FixTable(Lists.newArrayList("col1", "col2"), fixRows);

        Assert.assertEquals(Lists.newArrayList("val1", "val2"), row1.getOrderedColumns(fixTable.getColumnNames()));
        Assert.assertEquals(Lists.newArrayList("val11", "val22"), row2.getOrderedColumns(fixTable.getColumnNames()));
    }
    
}
