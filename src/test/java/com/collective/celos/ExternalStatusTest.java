package com.collective.celos;

import static com.collective.celos.SlotState.Status.FAILURE;
import static com.collective.celos.SlotState.Status.READY;
import static com.collective.celos.SlotState.Status.RUNNING;
import static com.collective.celos.SlotState.Status.SUCCESS;
import static com.collective.celos.SlotState.Status.TIMEOUT;
import static com.collective.celos.SlotState.Status.WAITING;

import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.SlotState.Status;

public class ExternalStatusTest {

    private ExternalStatus newExternalStatus(final Status status) {
        return new ExternalStatus() {
            @Override
            public Status getStatus() {
                return status;
            }
        };
    }

    @Test
    public void testIsRunning() {
        Assert.assertTrue(newExternalStatus(RUNNING).isRunning());
        Assert.assertFalse(newExternalStatus(WAITING).isRunning());
        Assert.assertFalse(newExternalStatus(TIMEOUT).isRunning());
        Assert.assertFalse(newExternalStatus(READY).isRunning());
        Assert.assertFalse(newExternalStatus(SUCCESS).isRunning());
        Assert.assertFalse(newExternalStatus(FAILURE).isRunning());
    }

    @Test
    public void testIsSuccess() {
        Assert.assertFalse(newExternalStatus(RUNNING).isSuccess());
        Assert.assertFalse(newExternalStatus(WAITING).isSuccess());
        Assert.assertFalse(newExternalStatus(TIMEOUT).isSuccess());
        Assert.assertFalse(newExternalStatus(READY).isSuccess());
        Assert.assertTrue(newExternalStatus(SUCCESS).isSuccess());
        Assert.assertFalse(newExternalStatus(FAILURE).isSuccess());
    }

    @Test
    public void testIsFailure() {
        Assert.assertFalse(newExternalStatus(RUNNING).isFailure());
        Assert.assertFalse(newExternalStatus(WAITING).isFailure());
        Assert.assertFalse(newExternalStatus(TIMEOUT).isFailure());
        Assert.assertFalse(newExternalStatus(READY).isFailure());
        Assert.assertFalse(newExternalStatus(SUCCESS).isFailure());
        Assert.assertTrue(newExternalStatus(FAILURE).isFailure());
    }

}
