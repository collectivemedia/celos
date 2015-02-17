package com.collective.celos.ci.mode.test.client;

import com.collective.celos.SlotState;
import com.collective.celos.WorkflowInfo;

import java.util.List;

public class WorkflowStatus {

    private final WorkflowInfo info;
    private final List<SlotState> slotStates;

    public WorkflowStatus(WorkflowInfo info, List<SlotState> slotStates) {
        this.slotStates = slotStates;
        this.info = info;
    }

    public WorkflowInfo getInfo() {
        return info;
    }

    public List<SlotState> getSlotStates() {
        return slotStates;
    }
}
