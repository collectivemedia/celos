package com.collective.celos.ci.testing.fixtures.convert;

import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 25.01.15.
 */
public class FixTableToJsonFileConverterTest {

    @Test
    public void testFixTableToJsonFileConverter() throws Exception {
        FixTableToJsonFileConverter converter = new FixTableToJsonFileConverter();
        List<FixTable.FixRow> fixRows = Lists.newArrayList();

        Map<String, String> map = Maps.newHashMap();
        map.put("col1", "val1");
        map.put("col2", "val2");

        Map<String, String> map2 = Maps.newHashMap();
        map2.put("col21", "val1");
        map2.put("col22", "val2");

        FixTable.FixRow row1 = new FixTable.FixRow(map);
        FixTable.FixRow row2 = new FixTable.FixRow(map2);
        fixRows.add(row1);
        fixRows.add(row2);
        FixTable fixTable = new FixTable(fixRows);
        FixFile fixFile = converter.convert(null, fixTable);

        String tableStr = IOUtils.toString(fixFile.getContent());
        String expectedStr =
                "{\"col1\":\"val1\",\"col2\":\"val2\"}\n" +
                "{\"col21\":\"val1\",\"col22\":\"val2\"}";

        Assert.assertEquals(expectedStr, tableStr);
    }
}
