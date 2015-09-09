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
package com.collective.celos.ci.config.deploy;

import com.collective.celos.Util;

import java.net.URI;

public class CelosCiTarget {

    private final URI pathToHdfsSite;
    private final URI pathToCoreSite;
    private final URI workflowsDirUri;
    private final URI defaultsDirUri;
    private final URI hiveJdbc;

    public CelosCiTarget(URI pathToHdfsSite, URI pathToCoreSite, URI workflowsDirUri, URI defaultsDirUri, URI hiveJdbc) {
        this.pathToHdfsSite = Util.requireNonNull(pathToHdfsSite);
        this.pathToCoreSite = Util.requireNonNull(pathToCoreSite);
        this.workflowsDirUri = Util.requireNonNull(workflowsDirUri);
        this.defaultsDirUri = Util.requireNonNull(defaultsDirUri);
        this.hiveJdbc = hiveJdbc;
    }

    public URI getPathToHdfsSite() {
        return pathToHdfsSite;
    }

    public URI getPathToCoreSite() {
        return pathToCoreSite;
    }

    public URI getWorkflowsDirUri() {
        return workflowsDirUri;
    }

    public URI getDefaultsDirUri() {
        return defaultsDirUri;
    }

    public URI getHiveJdbc() {
        return hiveJdbc;
    }
}

