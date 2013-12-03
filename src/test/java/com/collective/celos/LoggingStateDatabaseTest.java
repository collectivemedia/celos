package com.collective.celos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoggingStateDatabaseTest {

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
    public void putSlotState() throws Exception {
        StateDatabase stateDatabase = new LoggingStateDatabase(
                mock(StateDatabase.class));
        stateDatabase.putSlotState(slotState);

        assertEquals(
                "[workflow-1@2013-12-03T13:00Z] Changing status of slot workflow-1@2013-12-03T13:00Z to RUNNING with external ID = 2398721837913-2767321868713-W",
                testingLogger.getMessages()[0]);
    }

    @Test
    public void putGetState() throws Exception {
        StateDatabase stateDatabase = new LoggingStateDatabase(
                mock(StateDatabase.class));
        stateDatabase.getSlotState(slotId);

        assertEquals(0, testingLogger.getMessages().length);
    }

    @Test
    public void putSlotStateException() throws Exception {
        StateDatabase wrappedDatabase = mock(StateDatabase.class);
        Exception oops = new Exception("put failed");
        doThrow(oops).when(wrappedDatabase).putSlotState(slotState);

        StateDatabase stateDatabase = new LoggingStateDatabase(wrappedDatabase);
        try {
            stateDatabase.putSlotState(slotState);
        } catch (Exception e) {
            // Ignore
        }

        assertEquals(oops.getStackTrace().length + 2,
                testingLogger.getMessages().length);
        assertTrue(testingLogger.getMessages()[0]
                .contains("Changing status of slot"));
        assertTrue(testingLogger.getMessages()[1].contains(oops.toString()));
        for (String message : testingLogger.getMessages()) {
            assertTrue(message.contains("[workflow-1@2013-12-03T13:00Z]"));
        }
    }

    @Test
    public void putGetStateException() throws Exception {
        StateDatabase wrappedDatabase = mock(StateDatabase.class);
        Exception oops = new Exception("get failed");
        doThrow(oops).when(wrappedDatabase).getSlotState(slotId);

        StateDatabase stateDatabase = new LoggingStateDatabase(wrappedDatabase);
        try {
            stateDatabase.getSlotState(slotId);
        } catch (Exception e) {
            // Ignore
        }

        assertEquals(oops.getStackTrace().length + 1,
                testingLogger.getMessages().length);
        assertTrue(testingLogger.getMessages()[0].contains(oops.toString()));
        for (String message : testingLogger.getMessages()) {
            assertTrue(message.contains("[none]"));
        }
    }
}
