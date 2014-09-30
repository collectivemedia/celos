package com.collective.celos.ci.config.deploy;

public class CelosCiTarget {

    private final String scpSecuritySettings;
    private final String pathToHdfsSite;
    private final String pathToCoreSite;
    private final String celosWorkflowsDirUri;
    private final String defaultsFile;

    public CelosCiTarget(String scpSecuritySettings, String pathToHdfsSite, String pathToCoreSite, String celosWorkflowsDirUri, String defaultsFile) {
        this.scpSecuritySettings = scpSecuritySettings;
        this.pathToHdfsSite = pathToHdfsSite;
        this.pathToCoreSite = pathToCoreSite;
        this.celosWorkflowsDirUri = celosWorkflowsDirUri;
        this.defaultsFile = defaultsFile;
    }

    public String getScpSecuritySettings() {
        return scpSecuritySettings;
    }

    public String getPathToHdfsSite() {
        return pathToHdfsSite;
    }

    public String getPathToCoreSite() {
        return pathToCoreSite;
    }

    public String getCelosWorkflowsDirUri() {
        return celosWorkflowsDirUri;
    }

    public String getDefaultsFile() {
        return defaultsFile;
    }
}

