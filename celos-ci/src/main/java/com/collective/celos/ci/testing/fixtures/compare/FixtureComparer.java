package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;

/**
 * Created by akonopko on 10/10/14.
 */
public interface FixtureComparer<T extends FixObject> {

    public FixObjectCompareResult check(CelosCiContext context) throws Exception;

}
