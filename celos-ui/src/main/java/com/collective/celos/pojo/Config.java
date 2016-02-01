package com.collective.celos.pojo;

import java.util.List;

public class Config {

    private final List<WorkflowGroup> rows;

    public Config(List<WorkflowGroup> rows) {
        this.rows = rows;
    }

    public List<WorkflowGroup> getRows() {
        return rows;
    }

}

