package com.collective.celos.ci.fixtures.deploy;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.fixtures.processor.FileObjectTreeProcessor;
import com.collective.celos.ci.fixtures.structure.FixDir;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixDirDeployer {

    public abstract void deploy(FixDir fixDir) throws Exception;
}
