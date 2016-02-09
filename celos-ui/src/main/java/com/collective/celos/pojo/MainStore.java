package com.collective.celos.pojo;

import java.util.Collections;
import java.util.List;

public class MainStore {

    private final List<WorkflowGroup> rows;

    public MainStore(List<WorkflowGroup> rows) {
        this.rows = Collections.unmodifiableList(rows);
    }

    public List<WorkflowGroup> getRows() {
        return rows;
    }

}

