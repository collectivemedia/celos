package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirTreeConverter implements FixObjectCreator<FixDir> {

    private final FixObjectCreator creator;
    private final AbstractFixFileConverter fixFileConverter;

    public FixDirTreeConverter(FixObjectCreator creator, AbstractFixFileConverter fixFileConverter) {
        this.creator = creator;
        this.fixFileConverter = fixFileConverter;
    }

    private FixObject transform(TestRun tr, FixObject object, AbstractFixFileConverter converter) throws Exception {
        if (!object.isFile()) {
            FixDir fd = (FixDir) object;
            Map<String, FixObject> result = Maps.newHashMap();
            Map<String, FixObject> map = fd.getChildren();
            for(Map.Entry<String, FixObject> entry : map.entrySet()) {
                result.put(entry.getKey(), transform(tr, entry.getValue(), converter));
            }
            return new FixDir(result);
        } else {
            FixFile ff = (FixFile) object;
            return converter.convert(tr, ff);
        }
    }

    @Override
    public FixDir create(TestRun testRun) throws Exception {
        return transform(testRun, creator.create(testRun), fixFileConverter).asDir();
    }

    @Override
    public String getDescription(TestRun testRun) {
        return creator.getDescription(testRun);
    }

    public FixObjectCreator getCreator() {
        return creator;
    }

    public AbstractFixFileConverter getFixFileConverter() {
        return fixFileConverter;
    }
}
