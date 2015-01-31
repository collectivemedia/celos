package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixFileFromResourceCreator implements FixObjectCreator<FixFile> {

    private final Path relativePath;

    public FixFileFromResourceCreator(String path) {
        this.relativePath = Paths.get(path);
    }

    public FixFile create(TestRun testRun) throws Exception {
        File path = getPath(testRun);
        if (!path.isFile()) {
            throw new IllegalStateException("Cannot find file: " + path);
        }
        return new FixFile(new FileInputStream(path));
    }

    public File getPath(TestRun testRun) {
        return new File(testRun.getTestCasesDir(), relativePath.toString());
    }

    @Override
    public String getDescription(TestRun testRun) {
        return getPath(testRun).getAbsolutePath();
    }

}
