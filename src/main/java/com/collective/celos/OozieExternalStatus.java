package com.collective.celos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class OozieExternalStatus implements ExternalStatus {
    
    /*
     * Oozie workflow job states
     * 
     * - PREP, RUNNING, SUSPENDED, SUCCEEDED, KILLED and FAILED
     */
    private static final Set<String> universe = new HashSet<String>(
            Arrays.asList("PREP", "RUNNING", "SUSPENDED", "SUCCEEDED",
                    "KILLED", "FAILED"));

    private String externalStatus;

    public OozieExternalStatus(String externalStatus) {
        this.externalStatus = externalStatus;
        if (!universe.contains(externalStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status string: '" + externalStatus + "'");
        }
    }
        
    public boolean isRunning() { return externalStatus.equals("RUNNING") || externalStatus.equals("PREP"); } 

    public boolean isSuccess() { return externalStatus.equals("SUCCEEDED"); }

    public boolean isFailure() { return externalStatus.equals("FAILED") || externalStatus.equals("KILLED"); }
    
}
