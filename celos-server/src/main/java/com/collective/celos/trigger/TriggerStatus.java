package com.collective.celos.trigger;

import com.collective.celos.Util;

import java.util.List;

public final class TriggerStatus {

    private final String type;
    private final boolean ready;
    private final String description;
    private final List<TriggerStatus> subStatuses;

    public TriggerStatus(String triggerClassName, boolean ready, String description, List<TriggerStatus> subStatuses) {
        this.type = Util.requireNonNull(triggerClassName);
        this.ready = ready;
        this.description = Util.requireNonNull(description);
        this.subStatuses = Util.requireNonNull(subStatuses);
    }

    public String getType() {
        return type;
    }

    public boolean isReady() {
        return ready;
    }

    public String getDescription() {
        return description;
    }

    public List<TriggerStatus> getSubStatuses() {
        return subStatuses;
    }

}
