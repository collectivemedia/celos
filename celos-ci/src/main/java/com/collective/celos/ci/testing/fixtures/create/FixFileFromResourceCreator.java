package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.config.deploy.CelosCiContext;
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

    public FixFile create(CelosCiContext celosCiContext) throws Exception {
        File file = new File(path);
        if (!file.isFile()) {
            throw new IllegalStateException("Cannot find file: " + path);
        }
        return new FixFile(new FileInputStream(file));
    }

}
