package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ci.mode.test.TestRun;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFixObjectConverter<S extends FixObject, T extends FixObject> {

    public abstract T convert(TestRun tr, S ff) throws Exception;

}
