package com.collective.celos.ci.fixtures.processor;

import com.collective.celos.ci.fixtures.structure.FixFile;
import com.collective.celos.ci.fixtures.structure.FixObject;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFixObjectProcessor {

    public abstract void process(Path path, FixObject ff) throws IOException;

}
