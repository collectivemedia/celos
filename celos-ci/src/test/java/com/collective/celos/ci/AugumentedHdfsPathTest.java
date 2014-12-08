package com.collective.celos.ci;

import com.collective.celos.ci.testing.AugumentedHdfsPath;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

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
