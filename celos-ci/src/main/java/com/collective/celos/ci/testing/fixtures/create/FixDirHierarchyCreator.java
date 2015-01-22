package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirHierarchyCreator implements FixObjectCreator<FixDir> {

    private final Map<String, FixObjectCreator<FixFsObject>> hierarchy;

    public FixDirHierarchyCreator(Map<String, FixObjectCreator<FixFsObject>> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public FixDir create(TestRun testRun) throws Exception {
        Map<String, FixFsObject> content = Maps.newHashMap();
        for (Map.Entry<String, FixObjectCreator<FixFsObject>> entry : hierarchy.entrySet()) {
            content.put(entry.getKey(), entry.getValue().create(testRun));
        }
        return new FixDir(content);
    }

    @Override
    public String getDescription(TestRun testRun) {
        return hierarchy.keySet().toString();
    }

}
