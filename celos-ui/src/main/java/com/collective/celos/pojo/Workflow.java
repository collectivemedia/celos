package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class Workflow {

    private String workflowName;
    private List<Slot> slots;

    public Workflow(String workflowName) {
        this.workflowName = workflowName;
        this.slots = new ArrayList<>();
    }

    public Workflow setSlots(List<Slot> slots) {
        this.slots = slots;
        return this;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public List<Slot> getSlots() {
        return slots;
    }

}
