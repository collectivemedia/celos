package com.collective.celos.ui;

import com.collective.celos.Util;
import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * UI command-line options.
 */
public class UICommandLine {

    private final URL celosUrl;
    private final URL hueUrl;
    private final int port;
    private final File configFile;

    public UICommandLine(URL celosUrl, URL hueURL, int port, File configFile) {
        this.celosUrl =  Util.requireNonNull(celosUrl);
        this.configFile = configFile;
        this.hueUrl = hueURL;
        this.port = port;
    }

    public URL getCelosUrl() {
        return celosUrl;
    }

    public int getPort() {
        return port;
    }

    public URL getHueUrl() {
        return hueUrl;
    }

    public File getConfigFile() {
        return configFile;
    }
}
