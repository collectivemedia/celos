package com.collective.celos;

import static com.collective.celos.SlotState.Status.FAILURE;
import static com.collective.celos.SlotState.Status.RUNNING;
import static com.collective.celos.SlotState.Status.SUCCESS;

import org.junit.Assert;
import org.junit.Test;

public class OozieExternalStatusTest {

    @Test
    public void testGetStatus() {
        Assert.assertEquals(new OozieExternalStatus("PREP").getStatus(), null);
        Assert.assertEquals(new OozieExternalStatus("RUNNING").getStatus(), RUNNING);
        Assert.assertEquals(new OozieExternalStatus("SUSPENDED").getStatus(), null);
        Assert.assertEquals(new OozieExternalStatus("SUCCEEDED").getStatus(), SUCCESS);
        Assert.assertEquals(new OozieExternalStatus("KILLED").getStatus(), FAILURE);
        Assert.assertEquals(new OozieExternalStatus("FAILED").getStatus(), FAILURE);
    }

}
