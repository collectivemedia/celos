package com.collective.celos.ci.testing.fixtures.read;

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
public class FixFileTreeObjectCreator extends AbstractFixObjectCreator<FixObject> {

    private final String path;

    public FixFileTreeObjectCreator(String path) {
        this.path = path;
    }

    public FixObject create() throws Exception {
        return read(new File(path));
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
