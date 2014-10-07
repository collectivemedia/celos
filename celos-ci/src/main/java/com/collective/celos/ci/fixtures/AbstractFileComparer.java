package com.collective.celos.ci.fixtures;

import java.util.List;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFileComparer {

    private final List<AbstractFileDataReader> expectedResultsReaders;
    private final List<AbstractFileDataReader> actualResultsReaders;

    public AbstractFileComparer(List<AbstractFileDataReader> fileDataReaders, List<AbstractFileDataReader> actualResultsReaders) {
        this.expectedResultsReaders = fileDataReaders;
        this.actualResultsReaders = actualResultsReaders;
    }

    public abstract void compare() throws Exception;

    public List<AbstractFileDataReader> getExpectedResultsReaders() {
        return expectedResultsReaders;
    }

    public List<AbstractFileDataReader> getActualResultsReaders() {
        return actualResultsReaders;
    }
}
