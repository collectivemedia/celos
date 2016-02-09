package com.collective.celos.pojo;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkflowGroup {

    private final String name;
    private final List<String> times;
    private final List<String> days;
    private final List<Workflow> rows;

    private WorkflowGroup(String name, List<String> times, List<String> days, List<Workflow> rows) {
        this.name = name;
        this.times = Collections.unmodifiableList(times);
        this.days = Collections.unmodifiableList(days);
        this.rows = Collections.unmodifiableList(rows);
    }

    public WorkflowGroup(String name) {
        this.name = name;
        this.times = Collections.emptyList();
        this.days = Collections.emptyList();
        this.rows = Collections.emptyList();
    }

    public WorkflowGroup withTimes(List<String> times) {
        return new WorkflowGroup(this.name, times, this.days, this.rows);
    }

    public WorkflowGroup withDays(List<String> days) {
        return new WorkflowGroup(this.name, this.times, days, this.rows);
    }

    public WorkflowGroup withRows(List<Workflow> rows) {
        return new WorkflowGroup(this.name, this.times, this.days, rows);
    }

    public String getName() {
        return name;
    }

    public List<String> getTimes() {
        return times;
    }

    public List<String> getDays() {
        return days;
    }

    public List<Workflow> getRows() {
        return rows;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, rows, times, days);
    }

    @Override
    public boolean equals(Object tmp) {
        if (tmp instanceof WorkflowGroup) {
            final WorkflowGroup that = (WorkflowGroup) tmp;
            return Objects.equal(this.name, that.name)
                && Objects.equal(this.rows, that.rows)
                && Objects.equal(this.times, that.times)
                && Objects.equal(this.days, that.days);
        } else {
            return false;
        }
    }

    @Deprecated
    public String toString() {
        return null;
    }

}
