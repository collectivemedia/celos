package com.collective.celos.pojo;

import java.util.List;

public class Config {
    public List<WorkflowGroup> getRows() {
        return rows;
    }

    private List<WorkflowGroup> rows;

    public Config(List<WorkflowGroup> rows) {
        this.rows = rows;
    }

}

