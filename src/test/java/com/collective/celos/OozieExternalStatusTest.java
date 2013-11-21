package com.collective.celos;

import org.junit.Assert;
import org.junit.Test;

public class OozieExternalStatusTest {

    @Test
    public void testIsRunning() {
        Assert.assertEquals(new OozieExternalStatus("PREP").isRunning(), true);
        Assert.assertEquals(new OozieExternalStatus("RUNNING").isRunning(), true);
        Assert.assertEquals(new OozieExternalStatus("SUSPENDED").isRunning(), false);
        Assert.assertEquals(new OozieExternalStatus("SUCCEEDED").isRunning(), false);
        Assert.assertEquals(new OozieExternalStatus("KILLED").isRunning(), false);
        Assert.assertEquals(new OozieExternalStatus("FAILED").isRunning(), false);
    }

    @Test
    public void testIsSuccess() {
        Assert.assertEquals(new OozieExternalStatus("PREP").isSuccess(), false);
        Assert.assertEquals(new OozieExternalStatus("RUNNING").isSuccess(), false);
        Assert.assertEquals(new OozieExternalStatus("SUSPENDED").isSuccess(), false);
        Assert.assertEquals(new OozieExternalStatus("SUCCEEDED").isSuccess(), true);
        Assert.assertEquals(new OozieExternalStatus("KILLED").isSuccess(), false);
        Assert.assertEquals(new OozieExternalStatus("FAILED").isSuccess(), false);
    }

    @Test
    public void testIsFailure() {
        Assert.assertEquals(new OozieExternalStatus("PREP").isFailure(), false);
        Assert.assertEquals(new OozieExternalStatus("RUNNING").isFailure(), false);
        Assert.assertEquals(new OozieExternalStatus("SUSPENDED").isFailure(), false);
        Assert.assertEquals(new OozieExternalStatus("SUCCEEDED").isFailure(), false);
        Assert.assertEquals(new OozieExternalStatus("KILLED").isFailure(), true);
        Assert.assertEquals(new OozieExternalStatus("FAILED").isFailure(), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStatus() {
        new OozieExternalStatus("unexpected value");
    }

}
