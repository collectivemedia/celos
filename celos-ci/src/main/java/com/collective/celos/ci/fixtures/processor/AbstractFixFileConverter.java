package com.collective.celos.ci.fixtures.processor;

import com.collective.celos.ci.fixtures.structure.FixFile;

import java.io.IOException;

/**
 * Created by akonopko on 10/7/14.
 */
public interface AbstractFixFileConverter {

    public FixFile convert(FixFile ff) throws IOException;

}
