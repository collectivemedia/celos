package com.collective.celos.trigger;

import java.util.List;

public class TriggerStatusPOJO {

    public TriggerStatusPOJO(boolean ready, String description, List<TriggerStatusPOJO> subStatuses) {
        this.ready = ready;
        assert null != description;
        this.description = description;
        assert null != subStatuses;
        this.subStatuses = subStatuses;
    }


    public boolean isReady() {
        return ready;
    }

    public String getDescription() {
        return description;
    }

    public List<TriggerStatusPOJO> getSubStatuses() {
        return subStatuses;
    }

    boolean ready;
    String description;
    List<TriggerStatusPOJO> subStatuses;

}
