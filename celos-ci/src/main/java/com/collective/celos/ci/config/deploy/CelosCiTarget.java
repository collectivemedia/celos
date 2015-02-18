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
        this.hiveJdbc = hiveJdbc;
        this.pathToHdfsSite = Util.requireNonNull(pathToHdfsSite);
        this.pathToCoreSite = Util.requireNonNull(pathToCoreSite);
        this.workflowsDirUri = workflowsDirUri;
        this.defaultsDirUri = defaultsDirUri;
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

