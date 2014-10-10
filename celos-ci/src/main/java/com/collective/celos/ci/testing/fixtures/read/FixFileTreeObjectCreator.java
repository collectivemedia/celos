package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.outfixture.OutFixDir;
import com.collective.celos.ci.testing.structure.outfixture.OutFixFile;
import com.collective.celos.ci.testing.structure.outfixture.OutFixObject;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixFileTreeObjectCreator extends AbstractFixObjectCreator<OutFixObject, FixObject> {

    private final String path;
    private final FixObjectComparer<OutFixDir, FixDir> dirComparer;
    private final FixObjectComparer<OutFixFile, FixFile> fileComparer;

    public FixFileTreeObjectCreator(String path) {
        this(path, null, null);
    }

    public FixFileTreeObjectCreator(String path, FixObjectComparer<OutFixDir, FixDir> dirComparer, FixObjectComparer<OutFixFile, FixFile> fileComparer) {
        this.path = path;
        this.dirComparer = dirComparer == null ? DEFAULT_DIR_COMPARER : dirComparer;
        this.fileComparer = fileComparer == null ? DEFAULT_FILE_COMPARER : fileComparer;
    }

    @Override
    public FixObjectComparer<OutFixFile, FixFile> getFileComparer() {
        return fileComparer;
    }

    @Override
    public FixObjectComparer<OutFixDir, FixDir> getDirComparer() {
        return dirComparer;
    }

    @Override
    public OutFixObject createOutFixture() throws Exception {
        return read(new File(path), getOutFixObjectCreator());
    }

    @Override
    public FixObject createInFixture() throws Exception {
        return read(new File(path), getFixObjectCreator());
    }

    private <T extends FixObject> T read(File file, FixObjectsCreator<T> ctor) throws Exception {
        if (file.isDirectory()) {
            Map<String, T> content = Maps.newHashMap();
            for (File f : file.listFiles()) {
                content.put(f.getName(), read(f, ctor));
            }
            return ctor.createFixDir(content);
        } else {
            return ctor.createFixFile(new FileInputStream(file));
        }
    }

}
