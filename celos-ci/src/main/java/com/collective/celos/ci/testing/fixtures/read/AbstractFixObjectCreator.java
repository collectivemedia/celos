package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.testing.structure.fixobject.FixObject;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFixObjectCreator<T extends FixObject> {

    abstract T create() throws Exception;

}
