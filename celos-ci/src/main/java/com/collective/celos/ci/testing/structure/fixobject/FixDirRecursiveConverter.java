package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirRecursiveConverter extends AbstractFixObjectConverter<FixDir, FixDir> {

    private final AbstractFixObjectConverter<FixFile, FixFile> fixFileConverter;

    public FixDirRecursiveConverter(AbstractFixObjectConverter fixFileConverter) {
        this.fixFileConverter = fixFileConverter;
    }

    private FixFsObject transform(TestRun tr, FixFsObject object, AbstractFixObjectConverter<FixFile, FixFile> converter) throws Exception {
        if (!object.isFile()) {
            FixDir fd = (FixDir) object;
            Map<String, FixFsObject> result = Maps.newHashMap();
            Map<String, FixFsObject> map = fd.getChildren();
            for(Map.Entry<String, FixFsObject> entry : map.entrySet()) {
                result.put(entry.getKey(), transform(tr, entry.getValue(), converter));
            }
            return new FixDir(result);
        } else {
            FixFile ff = (FixFile) object;
            return converter.convert(tr, ff);
        }
    }

    @Override
    public FixDir convert(TestRun testRun, FixDir ff) throws Exception {
        return transform(testRun, ff, fixFileConverter).asDir();
    }

    public AbstractFixObjectConverter<FixFile, FixFile> getFixFileConverter() {
        return fixFileConverter;
    }
}
