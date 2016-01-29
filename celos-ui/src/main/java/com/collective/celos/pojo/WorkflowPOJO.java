package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class WorkflowPOJO {

    public String workflowName;
    public List<SlotPOJO> slots;

    public WorkflowPOJO(String workflowName) {
        this.workflowName = workflowName;
        this.slots = new ArrayList<>();
    }

    public WorkflowPOJO setSlots(List<SlotPOJO> slots) {
        this.slots = slots;
        return this;
    }

}
