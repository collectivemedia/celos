package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Maps;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirHierarchyCreator implements FixObjectCreator<FixDir> {

    private final Map<String, FixObject> hierarchy;

    public FixDirHierarchyCreator(Map<String, FixObject> hierarchy) {
        this.hierarchy = hierarchy;
    }

    public FixDir create(CelosCiContext context) {
        return new FixDir(hierarchy);
    }

}
