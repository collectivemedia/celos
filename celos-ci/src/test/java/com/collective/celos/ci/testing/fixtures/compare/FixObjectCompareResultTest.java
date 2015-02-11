package com.collective.celos.ci.testing.fixtures.compare;

import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by akonopko on 10/8/14.
 */
public class FixObjectCompareResultTest {

    @Test
    public void testDescription() throws IOException {
        Map<String, FixObjectCompareResult> children = Maps.newHashMap();
        FixObjectCompareResult e1 = FixObjectCompareResult.failed("fail");
        FixObjectCompareResult e2 = FixObjectCompareResult.SUCCESS;
        children.put("path1", e1);
        children.put("path2", e2);
        FixObjectCompareResult result = FixObjectCompareResult.wrapFailed(children, null);
        Assert.assertEquals(e2.getStatus(), FixObjectCompareResult.Status.SUCCESS);
        Assert.assertEquals(result.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(result.generateDescription(), "path1 : fail\n");
    }

    @Test
    public void testDescriptionWithMessage() throws IOException {
        Map<String, FixObjectCompareResult> children = Maps.newHashMap();
        FixObjectCompareResult e1 = FixObjectCompareResult.failed("fail");
        FixObjectCompareResult e2 = FixObjectCompareResult.SUCCESS;
        children.put("path1", e1);
        children.put("path2", e2);
        FixObjectCompareResult result = FixObjectCompareResult.wrapFailed(children, "my own error");
        Assert.assertEquals(result.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(result.generateDescription(), "my own error\n" +
                "path1 : fail\n");
    }

    @Test
    public void testDescriptionWithMessageMoreLayers() throws IOException {
        Map<String, FixObjectCompareResult> c1 = Maps.newHashMap();
        FixObjectCompareResult e1 = FixObjectCompareResult.failed("fail");
        FixObjectCompareResult e2 = FixObjectCompareResult.SUCCESS;
        c1.put("path1", e1);
        c1.put("path2", e2);
        FixObjectCompareResult e3 = FixObjectCompareResult.wrapFailed(c1, "my own error");

        Map<String, FixObjectCompareResult> c2 = Maps.newHashMap();
        c2.put("path3", e3);
        FixObjectCompareResult result = FixObjectCompareResult.wrapFailed(c2, null);
        Assert.assertEquals(result.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(result.generateDescription(), "path3/path1 : fail\n" +
                "path3 : my own error\n");
    }

}
