package com.collective.celos.pojo;

import java.util.ArrayList;
import java.util.List;

public class WorkflowGroupPOJO {

    public WorkflowGroupPOJO setTimes(List<String> times) {
        this.times = times;
        return this;
    }

    public WorkflowGroupPOJO setDays(List<String> days) {
        this.days = days;
        return this;
    }

    public WorkflowGroupPOJO setRows(List<WorkflowPOJO> rows) {
        this.rows = rows;
        return this;
    }

    public String name;
    public List<String> times;
    public List<String> days;
    public List<WorkflowPOJO> rows;

    public WorkflowGroupPOJO(String name) {
        this.name = name;
        this.times = new ArrayList<>();
        this.days = new ArrayList<>();
        this.rows = new ArrayList<>();
    }






}
