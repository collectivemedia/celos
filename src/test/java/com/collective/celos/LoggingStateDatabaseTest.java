package com.collective.celos;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;

public class LoggingStateDatabaseTest {

    @Rule
    public TestingLogger testingLogger = new TestingLogger();

    @Test
    public void putSlotState() throws Exception {

        // Target message:
        // Changing status of slot workflow-1@2013-12-03T13:00Z to RUNNING with
        // external ID = 2398721837913-2767321868713-W

        SlotID slotId = mock(SlotID.class);
        when(slotId.toString()).thenReturn("workflow-1@2013-12-03T13:00Z");

        SlotState slotState = mock(SlotState.class);
        when(slotState.getSlotID()).thenReturn(slotId);
        when(slotState.getStatus()).thenReturn(SlotState.Status.RUNNING);
        when(slotState.getExternalID()).thenReturn(
                "2398721837913-2767321868713-W");

        StateDatabase stateDatabase = new LoggingStateDatabase(
                mock(StateDatabase.class));
        stateDatabase.putSlotState(slotState);

        assertEquals(
                "Changing status of slot workflow-1@2013-12-03T13:00Z to RUNNING with external ID = 2398721837913-2767321868713-W",
                testingLogger.getMessages()[0]);
    }

    @Test
    public void putGetState() throws Exception {

         SlotID slotId = mock(SlotID.class);
         when(slotId.toString()).thenReturn("workflow-1@2013-12-03T13:00Z");
        
        StateDatabase stateDatabase = new LoggingStateDatabase(
                mock(StateDatabase.class));
        stateDatabase.getSlotState(slotId);

        assertEquals(0, testingLogger.getMessages().length);
    }

}
