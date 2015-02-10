package com.collective.celos.ci.testing.fixtures.convert;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FixTableToTSVFileConverterTest {

    @Test
    public void testFixTableToJsonFileConverter() throws Exception {
        FixTableToTSVFileConverter converter = new FixTableToTSVFileConverter();
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
        FixFile fixFile = converter.convert(null, fixTable);

        String tableStr = IOUtils.toString(fixFile.getContent());
        String expectedStr =
                "val1\tval2\n" +
                "val11\tval22\n";

        Assert.assertEquals(expectedStr, tableStr);
    }

}
