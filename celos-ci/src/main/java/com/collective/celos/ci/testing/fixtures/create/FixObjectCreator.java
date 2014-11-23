package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.CelosCi;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixObjectCreator<T extends FixObject> {

    T create(CelosCiContext celosCiContext) throws Exception;

}
