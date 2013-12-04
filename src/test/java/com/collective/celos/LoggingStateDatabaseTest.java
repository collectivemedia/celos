package com.collective.celos;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class LoggingStateDatabaseTest {

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
        SlotStateLogger stateLogger = mock(SlotStateLogger.class);
        StateDatabase stateDatabase = new LoggingStateDatabase(
                mock(StateDatabase.class), stateLogger);
        stateDatabase.putSlotState(slotState);

        verify(stateLogger)
                .info(slotState,
                        "Changing status of slot workflow-1@2013-12-03T13:00Z to RUNNING with external ID = 2398721837913-2767321868713-W");
        verifyNoMoreInteractions(stateLogger);
    }

    @Test
    public void getSlotState() throws Exception {
        SlotStateLogger stateLogger = mock(SlotStateLogger.class);
        StateDatabase stateDatabase = new LoggingStateDatabase(
                mock(StateDatabase.class), stateLogger);
        stateDatabase.getSlotState(slotId);
        verifyNoMoreInteractions(stateLogger);
    }

    @Test
    public void putSlotStateException() throws Exception {
        StateDatabase wrappedDatabase = mock(StateDatabase.class);
        Exception oops = new Exception("put failed");
        doThrow(oops).when(wrappedDatabase).putSlotState(slotState);

        SlotStateLogger stateLogger = mock(SlotStateLogger.class);
        StateDatabase stateDatabase = new LoggingStateDatabase(wrappedDatabase,
                stateLogger);

        try {
            stateDatabase.putSlotState(slotState);
        } catch (Exception e) {
            // Ignore
        }

        verify(stateLogger)
                .info(slotState,
                        "Changing status of slot workflow-1@2013-12-03T13:00Z to RUNNING with external ID = 2398721837913-2767321868713-W");
        verify(stateLogger).logException(slotState, oops);
        verifyNoMoreInteractions(stateLogger);
    }

    @Test
    public void getSlotStateException() throws Exception {
        StateDatabase wrappedDatabase = mock(StateDatabase.class);
        Exception oops = new Exception("get failed");
        doThrow(oops).when(wrappedDatabase).getSlotState(slotId);

        SlotStateLogger stateLogger = mock(SlotStateLogger.class);
        StateDatabase stateDatabase = new LoggingStateDatabase(wrappedDatabase,
                stateLogger);

        try {
            stateDatabase.getSlotState(slotId);
        } catch (Exception e) {
            // Ignore
        }

        verify(stateLogger).logException(null, oops);
        verifyNoMoreInteractions(stateLogger);
    }
}
