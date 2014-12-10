package com.collective.celos;

import junit.framework.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * Created by akonopko on 09.12.14.
 */
public class AugumentedHdfsPathTest {

    @Test
    public void testAugumentedHdfsPath() throws URISyntaxException {
        Assert.assertEquals(new AugumentedHdfsPath("/myprefix", "/originalPath/${var1}/path/${var2}").getPath(), "/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(new AugumentedHdfsPath("/myprefix", "hdfs:/originalPath/${var1}/path/${var2}").getPath(), "hdfs:/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(new AugumentedHdfsPath("/myprefix", "hdfs:///originalPath/${var1}/path/${var2}").getPath(), "hdfs:///myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(new AugumentedHdfsPath("/myprefix", "hdfs://nameservice/originalPath/${var1}/path/${var2}").getPath(), "hdfs://nameservice/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(new AugumentedHdfsPath("/myprefix", "hdfs://nameservice:2345/originalPath/${var1}/path/${var2}").getPath(), "hdfs://nameservice:2345/myprefix/originalPath/${var1}/path/${var2}");
    }

}
