package com.collective.celos.ci.fixtures.processor;

import com.collective.celos.ci.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.fixtures.structure.FixFile;

import java.io.IOException;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFixFileConverter {

    private final FixObjectComparer<FixFile> newComparer;

    protected AbstractFixFileConverter(FixObjectComparer<FixFile> newComparer) {
        this.newComparer = newComparer;
    }

    public abstract FixFile convert(FixFile ff) throws IOException;

    public FixObjectComparer<FixFile> getNewComparer() {
        return newComparer;
    }
}
