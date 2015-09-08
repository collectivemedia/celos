package com.collective.celos;

import java.util.List;

/**
 * Bean returned by HTTP API that contains information about a workflow and a subset of its slots.
 */
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
