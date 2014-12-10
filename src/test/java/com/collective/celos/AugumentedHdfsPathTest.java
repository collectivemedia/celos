package com.collective.celos;

import com.collective.celos.ci.Utils;
import junit.framework.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * Created by akonopko on 09.12.14.
 */
public class AugumentedHdfsPathTest {

    @Test
    public void testAugumentedHdfsPath() throws URISyntaxException {
        Assert.assertEquals(Util.augumentHdfsPath("/myprefix", "/originalPath/${var1}/path/${var2}"), "/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augumentHdfsPath("/myprefix", "hdfs:/originalPath/${var1}/path/${var2}"), "hdfs:/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augumentHdfsPath("/myprefix", "hdfs:///originalPath/${var1}/path/${var2}"), "hdfs:///myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augumentHdfsPath("/myprefix", "hdfs://nameservice/originalPath/${var1}/path/${var2}"), "hdfs://nameservice/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augumentHdfsPath("/myprefix", "hdfs://nameservice:2345/originalPath/${var1}/path/${var2}"), "hdfs://nameservice:2345/myprefix/originalPath/${var1}/path/${var2}");
    }

}
