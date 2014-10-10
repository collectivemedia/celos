package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.outfixture.OutFixDir;
import com.collective.celos.ci.testing.structure.outfixture.OutFixFile;
import com.collective.celos.ci.testing.structure.outfixture.OutFixObject;
import org.apache.commons.io.IOUtils;

/**
 * Created by akonopko on 10/7/14.
 */
public class StringFixObjectCreator extends AbstractFixObjectCreator {

    private final String content;

    public StringFixObjectCreator(String content) {
        this.content = content;
    }


    @Override
    public FixObjectComparer<OutFixFile, FixFile> getFileComparer() {
        return DEFAULT_FILE_COMPARER;
    }

    @Override
    public FixObjectComparer<OutFixDir, FixDir> getDirComparer() {
        return DEFAULT_DIR_COMPARER;
    }

    @Override
    public OutFixObject createOutFixture() throws Exception {
        return new OutFixFile(IOUtils.toInputStream(content), DEFAULT_FILE_COMPARER);
    }

    @Override
    public FixObject createInFixture() throws Exception {
        return new FixFile(IOUtils.toInputStream(content));
    }
}
