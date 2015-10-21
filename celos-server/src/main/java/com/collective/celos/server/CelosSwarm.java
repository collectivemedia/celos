package com.collective.celos.server;

import com.collective.celos.WorkflowID;

import java.net.URL;
import java.util.List;

/**
 * Created by akonopko on 21.10.15.
 */
public class CelosSwarm {

    public URL getCelosForWorkflow(WorkflowID id, List<URL> celoses) {
        int index = Math.abs(id.toString().hashCode()) % celoses.size();
        return celoses.get(index);
    }

}
