package com.collective.celos;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by akonopko on 09.12.14.
 */
public class AugumentedHdfsPathTest {

    @Test
    public void testAugumentedHdfsPath() {
        Assert.assertEquals(new AugumentedHdfsPath("/myprefix", "/originalPath").getPath(), "/myprefix/originalPath");
        Assert.assertEquals(new AugumentedHdfsPath("/myprefix", "hdfs:/originalPath").getPath(), "hdfs:/myprefix/originalPath");
        Assert.assertEquals(new AugumentedHdfsPath("/myprefix", "hdfs://nameservice/originalPath").getPath(), "hdfs://nameservice/myprefix/originalPath");
        Assert.assertEquals(new AugumentedHdfsPath("/myprefix", "hdfs://nameservice:2345/originalPath").getPath(), "hdfs://nameservice:2345/myprefix/originalPath");
    }

}
