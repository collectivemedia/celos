package com.collective.celos.pojo;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Workflow {

    private final String workflowName;
    private final List<Tile> rows;

    private Workflow(String workflowName, List<Tile> rows) {
        this.workflowName = workflowName;
        this.rows = Collections.unmodifiableList(rows);
    }

    public Workflow(String workflowName) {
        this(workflowName, Collections.emptyList());
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public List<Tile> getRows() {
        return rows;
    }

    public Workflow withRows(List<Tile> rows) {
        return new Workflow(this.workflowName, rows);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(workflowName, rows);
    }

    @Override
    public boolean equals(Object tmp) {
        if (tmp instanceof Workflow) {
            final Workflow that = (Workflow) tmp;
            return Objects.equal(this.workflowName, that.workflowName)
                && Objects.equal(this.rows, that.rows);
        } else {
            return false;
        }
    }

    @Deprecated
    public String toString() {
        return null;
    }

}
