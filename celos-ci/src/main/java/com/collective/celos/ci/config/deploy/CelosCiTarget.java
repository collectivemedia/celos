package com.collective.celos.ci.config.deploy;

import java.net.URI;

public class CelosCiTarget {

    private final String scpSecuritySettings;
    private final URI pathToHdfsSite;
    private final URI pathToCoreSite;
    private final URI celosWorkflowsDirUri;
    private final URI defaultsFile;

    public CelosCiTarget(String scpSecuritySettings, URI pathToHdfsSite, URI pathToCoreSite, URI celosWorkflowsDirUri, URI defaultsFile) {
        this.scpSecuritySettings = scpSecuritySettings;
        this.pathToHdfsSite = pathToHdfsSite;
        this.pathToCoreSite = pathToCoreSite;
        this.celosWorkflowsDirUri = celosWorkflowsDirUri;
        this.defaultsFile = defaultsFile;
    }

    public String getScpSecuritySettings() {
        return scpSecuritySettings;
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
}

