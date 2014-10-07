package com.collective.celos.ci.fixtures.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.structure.FixDir;
import com.collective.celos.ci.fixtures.processor.FileObjectTreeProcessor;

/**
 * Created by akonopko on 10/7/14.
 */
public class HdfsFixtureDeployer implements FixDirDeployer {

    private final CelosCiContext context;

    public HdfsFixtureDeployer(CelosCiContext context) {
        this.context = context;
    }

    public void deploy(FixDir fixDir) throws Exception {
        new FileObjectTreeProcessor().process(fixDir, new HdfsFixtureDeployerFileProcessor(context));
    }
}
