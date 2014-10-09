package com.collective.celos.ci.testing.fixtures.read;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.testing.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixObjectCreator {

    static final FixObjectComparer<FixDir> DEFAULT_DIR_COMPARER = new RecursiveDirComparer();
    static final FixObjectComparer<FixFile> DEFAULT_FILE_COMPARER = new PlainFileComparer();

    FixObject create() throws Exception;

}
