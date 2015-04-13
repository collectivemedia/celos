package com.collective.celos.ci.mode.test.client;

import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.collective.celos.WorkflowInfo;

import java.util.List;

public class WorkflowStatus {

    private final WorkflowID id;
    private final WorkflowInfo info;
    private final List<SlotState> slotStates;

    public WorkflowStatus(WorkflowID id, WorkflowInfo info, List<SlotState> slotStates) {
        this.id = id;
        this.slotStates = slotStates;
        this.info = info;
    }

    public WorkflowID getId() {
        return id;
    }

    public WorkflowInfo getInfo() {
        return info;
    }

    public List<SlotState> getSlotStates() {
        return slotStates;
    }
}
