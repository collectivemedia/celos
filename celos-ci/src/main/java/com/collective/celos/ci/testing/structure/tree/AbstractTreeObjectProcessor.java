package com.collective.celos.ci.testing.structure.tree;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractTreeObjectProcessor<T extends TreeObject> {

    public abstract void process(Path path, T ff) throws IOException;

}
