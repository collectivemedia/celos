package com.collective.celos.ci.testing.structure.fixobject;

import java.io.IOException;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFixFileConverter {

    public abstract FixFile convert(FixFile ff) throws IOException;

}
