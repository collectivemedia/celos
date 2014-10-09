package com.collective.celos.ci.fixtures.read;

import com.collective.celos.ci.fixtures.compare.FixObjectComparer;
import com.collective.celos.ci.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.fixtures.structure.FixDir;
import com.collective.celos.ci.fixtures.structure.FixFile;
import com.collective.celos.ci.fixtures.structure.FixObject;

import java.io.File;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FixObjectCreator {

    static final FixObjectComparer<FixDir> DEFAULT_DIR_COMPARER = new RecursiveDirComparer();
    static final FixObjectComparer<FixFile> DEFAULT_FILE_COMPARER = new PlainFileComparer();

    FixObject create() throws Exception;

}
