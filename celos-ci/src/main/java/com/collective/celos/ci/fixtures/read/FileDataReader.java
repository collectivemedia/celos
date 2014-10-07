package com.collective.celos.ci.fixtures.read;

import com.collective.celos.ci.fixtures.structure.FixObject;

import java.io.File;

/**
 * Created by akonopko on 10/7/14.
 */
public interface FileDataReader {

    public FixObject read() throws Exception;

}
