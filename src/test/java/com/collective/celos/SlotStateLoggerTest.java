package com.collective.celos;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SlotStateLoggerTest {

    @Rule
    public TestingLogger testingLogger = new TestingLogger();

    private SlotID slotId;
    private SlotState slotState;

    @Before
    public void setUp() {
        slotId = mock(SlotID.class);
        when(slotId.toString()).thenReturn("workflow-1@2013-12-03T13:00Z");

        slotState = mock(SlotState.class);
        when(slotState.getSlotID()).thenReturn(slotId);
        when(slotState.getStatus()).thenReturn(SlotState.Status.RUNNING);
        when(slotState.getExternalID()).thenReturn(
                "2398721837913-2767321868713-W");
    }

    @Test
    public void testInfo() {
        SlotStateLogger stateLogger = new SlotStateLogger(
                Logger.getLogger(getClass()));
        stateLogger.info(slotState, "foo");
        assertEquals("[workflow-1@2013-12-03T13:00Z] foo",
                testingLogger.getMessages()[0]);
    }

    @Test
    public void testInfoNullSotState() {
        SlotStateLogger stateLogger = new SlotStateLogger(
                Logger.getLogger(getClass()));
        stateLogger.info(null, "foo");
        assertEquals("[none] foo", testingLogger.getMessages()[0]);
    }

    @Test
    public void testDecorate() {
        SlotStateLogger stateLogger = new SlotStateLogger(
                Logger.getLogger(getClass()));
        assertEquals("[workflow-1@2013-12-03T13:00Z] ", stateLogger.decorate(slotState));
    }

    @Test
    public void testDecorateNullSlotState() {
        SlotStateLogger stateLogger = new SlotStateLogger(
                Logger.getLogger(getClass()));
        assertEquals("[none] ", stateLogger.decorate(null));
    }

    @Test
    public void testLogException() {
        SlotStateLogger stateLogger = new SlotStateLogger(
                Logger.getLogger(getClass()));
        Exception oops = new Exception("put failed");
        stateLogger.logException(slotState, oops);
        assertEquals(oops.getStackTrace().length + 1,
                testingLogger.getMessages().length);
        assertTrue(testingLogger.getMessages()[0].contains(oops.toString()));
        for (String message : testingLogger.getMessages()) {
            assertTrue(message.startsWith("[workflow-1@2013-12-03T13:00Z]"));
        }
    }

    @Test
    public void testLogExceptionNullSlotState() {
        SlotStateLogger stateLogger = new SlotStateLogger(
                Logger.getLogger(getClass()));
        Exception oops = new Exception("put failed");
        stateLogger.logException(null, oops);
        assertEquals(oops.getStackTrace().length + 1,
                testingLogger.getMessages().length);
        assertTrue(testingLogger.getMessages()[0].contains(oops.toString()));
        for (String message : testingLogger.getMessages()) {
            assertTrue(message.startsWith("[none]"));
        }
    }

}
