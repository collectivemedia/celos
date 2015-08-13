package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;

import java.net.URISyntaxException;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixObjectCreator<T extends FixObject> {

    T create(TestRun testRun) throws Exception;

    String getDescription(TestRun testRun) throws URISyntaxException;
}
