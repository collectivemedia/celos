package com.collective.celos.ci.fixtures.structure;

import java.io.InputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixFile implements FixObject {

    private final InputStream inputStream;

    public FixFile(InputStream inputStream) {
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
