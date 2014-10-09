package com.collective.celos.ci.fixtures.structure;

import com.collective.celos.ci.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.fixtures.compare.FixObjectComparer;

import java.io.InputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixFile extends FixObject {

    private final InputStream inputStream;

    public FixFile(InputStream inputStream, FixObjectComparer<FixFile> fileComparer) {
        super(fileComparer);
        this.inputStream = inputStream;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    public InputStream getContent() {
        return inputStream;
    }
}
