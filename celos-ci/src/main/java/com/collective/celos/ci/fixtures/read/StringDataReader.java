package com.collective.celos.ci.fixtures.read;

import com.collective.celos.ci.fixtures.structure.FixFile;
import com.collective.celos.ci.fixtures.structure.FixObject;
import org.apache.commons.io.IOUtils;

import java.io.StringReader;

/**
 * Created by akonopko on 10/7/14.
 */
public class StringDataReader implements FileDataReader {

    private final String content;

    public StringDataReader(String content) {
        this.content = content;
    }

    @Override
    public FixObject read() throws Exception {
        return new FixFile(IOUtils.toInputStream(content));
    }
}
