package com.collective.celos;

import static com.collective.celos.SlotState.Status.FAILURE;
import static com.collective.celos.SlotState.Status.RUNNING;
import static com.collective.celos.SlotState.Status.SUCCESS;
import static com.collective.celos.SlotState.Status.WAITING;

import org.junit.Assert;
import org.junit.Test;

public class OozieExternalStatusTest {

    @Test
    public void testGetStatus() {
        Assert.assertEquals(new OozieExternalStatus("PREP").getStatus(), WAITING);
        Assert.assertEquals(new OozieExternalStatus("RUNNING").getStatus(), RUNNING);
        Assert.assertEquals(new OozieExternalStatus("SUSPENDED").getStatus(), WAITING);
        Assert.assertEquals(new OozieExternalStatus("SUCCEEDED").getStatus(), SUCCESS);
        Assert.assertEquals(new OozieExternalStatus("KILLED").getStatus(), FAILURE);
        Assert.assertEquals(new OozieExternalStatus("FAILED").getStatus(), FAILURE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStatus() {
        new OozieExternalStatus("unexpected value");
    }

}
