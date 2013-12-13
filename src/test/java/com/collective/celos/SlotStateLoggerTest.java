package com.collective.celos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SlotStateLoggerTest {

    @Rule
    public TestingLogger testingLogger = new TestingLogger();

    private SlotID slotId;
//    private SlotState slotState;
    private WorkflowID workflowId;

    @Before
    public void setUp() {
        slotId = mock(SlotID.class);
        when(slotId.toString()).thenReturn("slot-id");

        workflowId = mock(WorkflowID.class);
        when(workflowId.toString()).thenReturn("workflow-id");
    }

    @Test
    public void testLogMessageWorkflowID() {
        SlotStateLogger stateLogger = new SlotStateLogger();
        stateLogger.logMessage(workflowId, "oops");
        assertEquals(1,
                testingLogger.getMessages().length);
        assertEquals("[workflow-id] oops", testingLogger.getMessages()[0]);
    }

    @Test
    public void testLogMessageSlotID() {
        SlotStateLogger stateLogger = new SlotStateLogger();
        stateLogger.logMessage(slotId, "oops");
        assertEquals(1,
                testingLogger.getMessages().length);
        assertEquals("[slot-id] oops", testingLogger.getMessages()[0]);
    }

    @Test
    public void testLogExceptionWorkflowID() {
        SlotStateLogger stateLogger = new SlotStateLogger();
        Exception oops = new Exception("put failed");
        stateLogger.logException(workflowId, oops);
        assertEquals(oops.getStackTrace().length + 1,
                testingLogger.getMessages().length);
        assertTrue(testingLogger.getMessages()[0].contains(oops.toString()));
        for (String message : testingLogger.getMessages()) {
            assertTrue(message.startsWith("[workflow-id]"));
        }
    }

    @Test
    public void testLogExceptionSlotID() {
        SlotStateLogger stateLogger = new SlotStateLogger();
        Exception oops = new Exception("put failed");
        stateLogger.logException(slotId, oops);
        assertEquals(oops.getStackTrace().length + 1,
                testingLogger.getMessages().length);
        assertTrue(testingLogger.getMessages()[0].contains(oops.toString()));
        for (String message : testingLogger.getMessages()) {
            assertTrue(message.startsWith("[slot-id]"));
        }
    }

    @Test
    public void testFormat() {
        SlotStateLogger stateLogger = new SlotStateLogger();
        assertEquals("[foo] bar", stateLogger.format("foo", "bar", " "));
        assertEquals("[none] bar", stateLogger.format(null, "bar", " "));
        assertEquals("[foo] none", stateLogger.format("foo", null, " "));
        assertEquals("[foo]: bar", stateLogger.format("foo", "bar", ": "));
    }

}
