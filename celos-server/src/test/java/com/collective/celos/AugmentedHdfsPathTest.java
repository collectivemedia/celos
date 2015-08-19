package com.collective.celos;

import junit.framework.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * Created by akonopko on 09.12.14.
 */
public class AugmentedHdfsPathTest {

    @Test
    public void testAugmentedHdfsPath() throws URISyntaxException {
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "/originalPath/${var1}/path/${var2}"), "/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "hdfs:/originalPath/${var1}/path/${var2}"), "hdfs:/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "hdfs:///originalPath/${var1}/path/${var2}"), "hdfs:///myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "hdfs://nameservice/originalPath/${var1}/path/${var2}"), "hdfs://nameservice/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "hdfs://nameservice:2345/originalPath/${var1}/path/${var2}"), "hdfs://nameservice:2345/myprefix/originalPath/${var1}/path/${var2}");

        Assert.assertEquals(Util.augmentHdfsPath("/", "/originalPath/${var1}/path/${var2}"), "/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/", "hdfs:/originalPath/${var1}/path/${var2}"), "hdfs:/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/", "hdfs:///originalPath/${var1}/path/${var2}"), "hdfs:///originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/", "hdfs://nameservice/originalPath/${var1}/path/${var2}"), "hdfs://nameservice/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/", "hdfs://nameservice:2345/originalPath/${var1}/path/${var2}"), "hdfs://nameservice:2345/originalPath/${var1}/path/${var2}");
    }

}
