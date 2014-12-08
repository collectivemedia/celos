package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirHierarchyCreator implements FixObjectCreator<FixDir> {

    private final Map<String, FixObject> hierarchy;

    public FixDirHierarchyCreator(Map<String, FixObject> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public FixDir create(TestRun testRun) {
        return new FixDir(hierarchy);
    }

    @Override
    public String getDescription(TestRun testRun) {
        return hierarchy.keySet().toString();
    }

}
