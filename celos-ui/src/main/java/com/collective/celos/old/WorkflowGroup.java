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
package com.collective.celos.old;

import java.util.Collections;
import java.util.List;

import com.collective.celos.Util;
import com.collective.celos.WorkflowID;

/**
 * A named group of rows, for rendering the rows list.
 */
public class WorkflowGroup {
    
    private final String name;
    private final List<WorkflowID> rows;
    
    public WorkflowGroup(String name, List<WorkflowID> workflows) {
        this.name = Util.requireNonNull(name);
        this.rows = Collections.unmodifiableList(Util.requireNonNull(workflows));
    }

    public String getName() {
        return name;
    }

    public List<WorkflowID> getRows() {
        return rows;
    }
    
}
