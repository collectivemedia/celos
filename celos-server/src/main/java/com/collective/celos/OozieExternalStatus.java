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

import static org.apache.oozie.client.WorkflowJob.Status.PREP;
import static org.apache.oozie.client.WorkflowJob.Status.RUNNING;
import static org.apache.oozie.client.WorkflowJob.Status.SUCCEEDED;

import org.apache.oozie.client.WorkflowJob.Status;

/**
 * Status of Oozie execution of a workflow.
 */
public class OozieExternalStatus implements ExternalStatus {

    private Status status;

    public OozieExternalStatus(String statusString) {
        this.status = Status.valueOf(statusString);
        if (status == null) {
            throw new IllegalArgumentException("Invalid status string: '"
                    + statusString + "'");
        }
    }

    public boolean isRunning() {
        return status == RUNNING || status == PREP;
    }

    public boolean isSuccess() {
        return status == SUCCEEDED;
    }

}
