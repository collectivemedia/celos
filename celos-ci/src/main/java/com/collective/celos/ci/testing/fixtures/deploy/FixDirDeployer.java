package com.collective.celos.ci.testing.fixtures.deploy;

import com.collective.celos.ci.testing.structure.fixobject.FixDir;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixDirDeployer {

    public abstract void deploy(FixDir fixDir) throws Exception;
}
