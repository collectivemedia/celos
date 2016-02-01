package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class WorkflowGroup {

    private String name;
    private List<String> times;
    private List<String> days;
    private List<Workflow> rows;

    public WorkflowGroup(String name) {
        this.name = name;
        this.times = new ArrayList<>();
        this.days = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public WorkflowGroup setTimes(List<String> times) {
        this.times = times;
        return this;
    }

    public WorkflowGroup setDays(List<String> days) {
        this.days = days;
        return this;
    }

    public WorkflowGroup setRows(List<Workflow> rows) {
        this.rows = rows;
        return this;
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
