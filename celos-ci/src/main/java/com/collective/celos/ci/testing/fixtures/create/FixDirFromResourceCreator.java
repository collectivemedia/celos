package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirFromResourceCreator implements FixObjectCreator<FixDir> {

    private final File path;

    public FixDirFromResourceCreator(File testCasesDir, String path) {
        this.path = new File(testCasesDir, path);
    }

    public File getPath() {
        return path;
    }

    public FixDir create(TestRun testRun) throws Exception {
        if (!path.isDirectory()) {
            throw new IllegalStateException("Cannot find directory: " + path);
        }
        return read(path).asDir();
    }

    private FixObject read(File file) throws Exception {
        if (file.isDirectory()) {
            Map<String, FixObject> content = Maps.newHashMap();
            for (File f : file.listFiles()) {
                content.put(f.getName(), read(f));
            }
            return new FixDir(content);
        } else {
            return new FixFile(new FileInputStream(file));
        }
    }

}
