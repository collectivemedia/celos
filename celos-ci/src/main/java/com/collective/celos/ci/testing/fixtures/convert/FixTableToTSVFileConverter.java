package com.collective.celos.ci.testing.fixtures.convert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.AbstractFixObjectConverter;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;

public class FixTableToTSVFileConverter extends AbstractFixObjectConverter<FixTable,FixFile> {

    @Override
    public FixFile convert(TestRun tr, FixTable table) throws Exception {
        List<String> lines = new ArrayList<>();
        for (FixTable.FixRow row : table.getRows()) {
            lines.add(StringUtils.join(row.getOrderedColumns(table.getColumnNames()), "\t"));
        }
        return new FixFile(IOUtils.toInputStream(StringUtils.join(lines, "\n") + "\n"));
    }
    
}
