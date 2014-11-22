package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.testing.structure.fixobject.FixFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixFileFromResourceCreator implements FixObjectCreator<FixFile> {

    private final String path;

    public FixFileFromResourceCreator(String path) {
        this.path = path;
    }

    public FixFile create() throws Exception {
        File file = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());
        if (!file.isFile()) {
            throw new IllegalStateException("Cannot find file: " + path);
        }
        return new FixFile(new FileInputStream(file));
    }

}
