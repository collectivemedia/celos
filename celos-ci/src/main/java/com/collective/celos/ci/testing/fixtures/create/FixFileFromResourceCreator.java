package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixFileFromResourceCreator implements FixObjectCreator<FixFile> {

    private final File path;

    public FixFileFromResourceCreator(File testCasesDir, String path) {
        this.path = new File(testCasesDir, path);
    }

    public FixFile create(CelosCiContext celosCiContext) throws Exception {
        if (!path.isFile()) {
            throw new IllegalStateException("Cannot find file: " + path);
        }
        return new FixFile(new FileInputStream(path));
    }

}
