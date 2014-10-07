package com.collective.celos.ci.fixtures;

import java.util.List;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFileDeployer {

    private final List<AbstractFileDataReader> readers;

    public AbstractFileDeployer(List<AbstractFileDataReader> readers) {
        this.readers = readers;
    }

    public List<AbstractFileDataReader> getReaders() {
        return readers;
    }

    public abstract void deploy() throws Exception;

}
