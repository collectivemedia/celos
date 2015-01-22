package com.collective.celos.ci.config.deploy;

import com.collective.celos.Util;

import java.net.URI;

public class CelosCiTarget {

    private final URI pathToHdfsSite;
    private final URI pathToCoreSite;
    private final URI celosWorkflowsDirUri;
    private final URI defaultsFile;
    private final URI hiveJdbc;

    public CelosCiTarget(URI pathToHdfsSite, URI pathToCoreSite, URI celosWorkflowsDirUri, URI defaultsFile, URI hiveJdbc) {
        this.hiveJdbc = hiveJdbc;
        this.pathToHdfsSite = Util.requireNonNull(pathToHdfsSite);
        this.pathToCoreSite = Util.requireNonNull(pathToCoreSite);
        this.celosWorkflowsDirUri = celosWorkflowsDirUri;
        this.defaultsFile = defaultsFile;
    }

    public URI getPathToHdfsSite() {
        return pathToHdfsSite;
    }

    public URI getPathToCoreSite() {
        return pathToCoreSite;
    }

    public URI getCelosWorkflowsDirUri() {
        return celosWorkflowsDirUri;
    }

    public URI getDefaultsFile() {
        return defaultsFile;
    }

    public URI getHiveJdbc() {
        return hiveJdbc;
    }
}

