package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class Workflow {

    private final String workflowName;
    private final List<Slot> rows;

    private Workflow(List<Slot> rows, String workflowName) {
        this.rows = rows;
        this.workflowName = workflowName;
    }

    public Workflow(String workflowName) {
        this.workflowName = workflowName;
        this.rows = new ArrayList<>();
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public List<Slot> getRows() {
        return rows;
    }

    public Workflow withRows(List<Slot> rows) {
        return new Workflow(rows, this.workflowName);
    }

}
