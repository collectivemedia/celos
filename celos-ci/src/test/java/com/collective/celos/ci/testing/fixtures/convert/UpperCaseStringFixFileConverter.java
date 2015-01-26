package com.collective.celos.ci.testing.fixtures.convert;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.structure.fixobject.AbstractFixObjectConverter;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class UpperCaseStringFixFileConverter extends AbstractFixObjectConverter<FixFile, FixFile> {

        @Override
        public FixFile convert(TestRun tr, FixFile ff) throws IOException {
            String newContent = IOUtils.toString(ff.getContent()).toUpperCase();
            return new FixFile(IOUtils.toInputStream(newContent));
        }
    }
