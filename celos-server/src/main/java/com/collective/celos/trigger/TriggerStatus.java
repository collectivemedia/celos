package com.collective.celos.trigger;

import com.collective.celos.Util;

import java.util.List;

public final class TriggerStatus {

    final boolean ready;
    final String description;
    final List<TriggerStatus> subStatuses;

    public TriggerStatus(boolean ready, String description, List<TriggerStatus> subStatuses) {
        this.ready = ready;
        this.description = Util.requireNonNull(description);
        this.subStatuses = Util.requireNonNull(subStatuses);
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
