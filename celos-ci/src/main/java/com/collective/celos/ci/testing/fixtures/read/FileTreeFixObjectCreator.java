package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
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
public class FileTreeFixObjectCreator implements FixObjectCreator {

    private final String path;
    private final FixObjectComparer<FixDir> dirComparer;
    private final FixObjectComparer<FixFile> fileComparer;

    public FileTreeFixObjectCreator(String path) {
        this(path, null, null);
    }

    public FileTreeFixObjectCreator(String path, FixObjectComparer<FixDir> dirComparer, FixObjectComparer<FixFile> fileComparer) {
        this.path = path;
        this.dirComparer = dirComparer == null ? DEFAULT_DIR_COMPARER : dirComparer;
        this.fileComparer = fileComparer == null ? DEFAULT_FILE_COMPARER : fileComparer;
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
            return new FixDir(content, dirComparer);
        } else {
            return new FixFile(new FileInputStream(file), fileComparer);
        }
    }

}
