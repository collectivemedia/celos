package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class WorkflowPOJO {

    public String workflowName;
    public List<SlotPOJO> rows;

    public WorkflowPOJO(String workflowName) {
        this.workflowName = workflowName;
        this.rows = new ArrayList<>();
    }

    public WorkflowPOJO setRows(List<SlotPOJO> rows) {
        this.rows = rows;
        return this;
    }

}
