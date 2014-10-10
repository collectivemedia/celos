package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.outfixture.OutFixDir;
import com.collective.celos.ci.testing.structure.outfixture.OutFixFile;
import com.collective.celos.ci.testing.structure.outfixture.OutFixObject;
import org.apache.hadoop.fs.FileStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFixObjectCreator {

    static final FixObjectComparer<OutFixDir, FixDir> DEFAULT_DIR_COMPARER = new RecursiveDirComparer();
    static final FixObjectComparer<OutFixFile, FixFile> DEFAULT_FILE_COMPARER = new PlainFileComparer();

    static interface FixObjectsCreator<T extends FixObject> {

        public T createFixFile(InputStream inputStream) throws IOException;

        public T createFixDir(Map<String, T> content);

    }

    public FixObjectsCreator<OutFixObject> getOutFixObjectCreator() {

        return new FixObjectsCreator<OutFixObject>() {
            @Override
            public OutFixObject createFixFile(InputStream is) throws IOException {
                return new OutFixFile(is, getFileComparer());
            }

            @Override
            public OutFixObject createFixDir(Map<String, OutFixObject> content) {
                return new OutFixDir(content, getDirComparer());
            }
        };
    }

    public FixObjectsCreator<FixObject> getFixObjectCreator() {

        return new FixObjectsCreator<FixObject>() {
            @Override
            public FixObject createFixFile(InputStream is) throws IOException {
                return new FixFile(is);
            }

            @Override
            public FixObject createFixDir(Map<String, FixObject> content) {
                return new FixDir(content);
            }
        };
    }

    public abstract FixObjectComparer<OutFixFile, FixFile> getFileComparer();

    public abstract FixObjectComparer<OutFixDir, FixDir> getDirComparer();

    public abstract OutFixObject createOutFixture() throws Exception;

    public abstract FixObject createInFixture() throws Exception;

}
