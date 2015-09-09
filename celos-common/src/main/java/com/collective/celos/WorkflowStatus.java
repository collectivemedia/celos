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

import java.util.List;

/**
 * Bean returned by HTTP API that contains information about a workflow and a subset of its slots.
 */
public class WorkflowStatus {

    private final WorkflowInfo info;
    private final List<SlotState> slotStates;

    public WorkflowStatus(WorkflowInfo info, List<SlotState> slotStates) {
        this.slotStates = slotStates;
        this.info = info;
    }

    public WorkflowInfo getInfo() {
        return info;
    }

    public List<SlotState> getSlotStates() {
        return slotStates;
    }
}
