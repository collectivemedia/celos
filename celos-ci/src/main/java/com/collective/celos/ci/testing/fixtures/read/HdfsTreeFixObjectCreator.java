package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.outfixture.OutFixDir;
import com.collective.celos.ci.testing.structure.outfixture.OutFixFile;
import com.collective.celos.ci.testing.structure.outfixture.OutFixObject;
import com.google.common.collect.Maps;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class HdfsTreeFixObjectCreator extends AbstractFixObjectCreator<OutFixObject, FixObject> {

    private final CelosCiContext context;
    private final String path;
    private final FixObjectComparer<OutFixDir, FixDir> dirComparer;
    private final FixObjectComparer<OutFixFile, FixFile> fileComparer;

    public HdfsTreeFixObjectCreator(CelosCiContext context, String path) {
        this(context, path, null, null);
    }

    public HdfsTreeFixObjectCreator(CelosCiContext context, String path, FixObjectComparer<OutFixDir, FixDir> dirComparer, FixObjectComparer<OutFixFile, FixFile> fileComparer) {
        this.context = context;
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
        return read(new Path(path), getOutFixObjectCreator());
    }

    @Override
    public FixObject createInFixture() throws Exception {
        return read(new Path(path), getFixObjectCreator());
    }

    private <T extends FixObject> T read(Path path, FixObjectsCreator<T> ctor) throws Exception {
        FileStatus fileStatus = context.getFileSystem().getFileStatus(path);
        if (fileStatus.isDirectory()) {
            Map<String, T> content = Maps.newHashMap();
            FileStatus[] statuses = context.getFileSystem().listStatus(fileStatus.getPath());
            for (int i=0; i < statuses.length; i++) {
                FileStatus childStatus = statuses[i];
                T fixObject = read(childStatus.getPath(), ctor);
                content.put(childStatus.getPath().getName(), fixObject);
            }
            return ctor.createFixDir(content);
        } else {
            return ctor.createFixFile(context.getFileSystem().open(fileStatus.getPath()));
        }
    }

}
