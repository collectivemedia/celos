package com.collective.celos.ci.testing.fixtures.create;

import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * Created by akonopko on 10/7/14.
 */
public class StringFixObjectCreatorTest {

    public static final String SOME_TEXT = "some test";

    @Test
    public void testStringFixObjectCreator() throws Exception {
        FixFileFromStringCreator creator = new FixFileFromStringCreator(SOME_TEXT);
        FixFile fixFile = creator.create();
        Assert.assertEquals(SOME_TEXT, IOUtils.toString(fixFile.getContent()));
    }

}
