package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirHierarchyCreator implements FixObjectCreator<FixDir> {

    private final Map<String, FixObjectCreator> hierarchy;

    public FixDirHierarchyCreator(Map<String, FixObjectCreator> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public FixDir create(TestRun testRun) throws Exception {
        Map<String, FixObject> content = Maps.newHashMap();
        for (Map.Entry<String, FixObjectCreator> entry : hierarchy.entrySet()) {
            content.put(entry.getKey(), entry.getValue().create(testRun));
        }
        return new FixDir(content);
    }

    @Override
    public String getDescription(TestRun testRun) {
        return hierarchy.keySet().toString();
    }

}
