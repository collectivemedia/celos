package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class Workflow {

    private String workflowName;
    private List<Slot> rows;

    public Workflow(String workflowName) {
        this.workflowName = workflowName;
        this.rows = new ArrayList<>();
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public List<Slot> getSlots() {
        return rows;
    }

    public Workflow setRows(List<Slot> rows) {
        this.rows = rows;
        return this;
    }

}
