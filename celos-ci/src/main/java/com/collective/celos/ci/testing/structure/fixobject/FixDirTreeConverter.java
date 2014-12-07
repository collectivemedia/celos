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
    private final AbstractFixFileConverter converter;

    public FixDirTreeConverter(FixObjectCreator creator, AbstractFixFileConverter converter) {
        this.creator = creator;
        this.converter = converter;
    }

    private FixObject transform(FixObject object, AbstractFixFileConverter converter) throws IOException {
        if (!object.isFile()) {
            FixDir fd = (FixDir) object;
            Map<String, FixObject> result = Maps.newHashMap();
            Map<String, FixObject> map = fd.getChildren();
            for(Map.Entry<String, FixObject> entry : map.entrySet()) {
                result.put(entry.getKey(), transform(entry.getValue(), converter));
            }
            return new FixDir(result);
        } else {
            FixFile ff = (FixFile) object;
            return converter.convert(ff);
        }
    }

    @Override
    public FixDir create(TestRun testRun) throws Exception {
        return transform(creator.create(testRun), converter).asDir();
    }

    @Override
    public String getDescription(TestRun testRun) {
        return creator.getDescription(testRun);
    }
}
