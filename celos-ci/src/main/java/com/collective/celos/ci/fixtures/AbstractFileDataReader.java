package com.collective.celos.ci.fixtures;

import java.util.List;

/**
 * Created by akonopko on 10/7/14.
 */
public abstract class AbstractFileDataReader {

    public abstract List<FileInfo> read() throws Exception;

}
