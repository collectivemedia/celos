package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class WorkflowGroup {

    private final String name;
    private final List<String> times;
    private final List<String> days;
    private final List<Workflow> rows;

    private WorkflowGroup(String name, List<String> times, List<String> days, List<Workflow> rows) {
        this.name = name;
        this.times = times;
        this.days = days;
        this.rows = rows;
    }

    public WorkflowGroup(String name) {
        this.name = name;
        this.times = new ArrayList<>();
        this.days = new ArrayList<>();
        this.rows = new ArrayList<>();
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

}
