/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The set of all workflows loaded into the scheduler.
 */
public class WorkflowConfiguration {

    private final Map<WorkflowID, Workflow> workflows = new HashMap<>();

    public Collection<Workflow> getWorkflows() {
        return workflows.values();
    }
    
    public Workflow findWorkflow(WorkflowID id) {
        return workflows.get(Util.requireNonNull(id));
    }

    public void addWorkflow(Workflow wf) {
        Util.requireNonNull(wf);
        WorkflowID id = wf.getID();
        if (findWorkflow(id) != null) {
            throw new IllegalArgumentException("Workflow with this ID already exists: " + id);
        }
        workflows.put(id, wf);
    }
    
}
