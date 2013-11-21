package com.collective.celos;

import static com.collective.celos.SlotState.Status.FAILURE;
import static com.collective.celos.SlotState.Status.RUNNING;
import static com.collective.celos.SlotState.Status.SUCCESS;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.collective.celos.SlotState.Status;

public class OozieExternalStatus extends ExternalStatus {
    
    /*
     * Oozie workflow job states
     * 
     * - PREP, RUNNING, SUSPENDED, SUCCEEDED, KILLED and FAILED
     */
    private static final Map<String, Status> statusMap;
    static {
        Map<String, Status> map = new HashMap<String, Status>();
        map.put("PREP", null);
        map.put("RUNNING", RUNNING);
        map.put("SUSPENDED", null);
        map.put("SUCCEEDED", SUCCESS);
        map.put("KILLED", FAILURE);
        map.put("FAILED", FAILURE);
        statusMap = Collections.unmodifiableMap(map);
    }

    private final Status status;

    public OozieExternalStatus(String oozieStatusString) {
        this.status = statusMap.get(oozieStatusString);
    }
    
    public Status getStatus() {
        return status;
    }
    
}
