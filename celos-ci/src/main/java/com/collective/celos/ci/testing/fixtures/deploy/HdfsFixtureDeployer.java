package com.collective.celos.ci.testing.fixtures.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.structure.tree.TreeStructureProcessor;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;

/**
 * Created by akonopko on 10/7/14.
 */
public class HdfsFixtureDeployer implements FixDirDeployer {

    private final CelosCiContext context;

    public HdfsFixtureDeployer(CelosCiContext context) {
        this.context = context;
    }

    public void deploy(FixDir fixDir) throws Exception {
        new TreeStructureProcessor().process(fixDir, new HdfsFixtureDeployerFileProcessor(context));
    }
}
