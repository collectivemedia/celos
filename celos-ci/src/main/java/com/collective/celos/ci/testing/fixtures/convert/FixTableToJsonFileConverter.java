package com.collective.celos.ci.testing.fixtures.convert;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.AbstractFixObjectConverter;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by akonopko on 22.01.15.
 */
public class FixTableToJsonFileConverter extends AbstractFixObjectConverter<FixTable,FixFile> {

    private final Gson gson = new GsonBuilder().create();

    @Override
    public FixFile convert(TestRun tr, FixTable ff) throws Exception {
        List<String> jsonStrs = Lists.newArrayList();
        for (FixTable.FixRow fr : ff.getRows()) {
            jsonStrs.add(gson.toJson(fr.getCells()));
        }
        return new FixFile(IOUtils.toInputStream(StringUtils.join(jsonStrs, "\n")));
    }
}
