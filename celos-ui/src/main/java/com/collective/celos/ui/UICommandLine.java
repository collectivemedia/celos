package com.collective.celos.ui;

import com.collective.celos.Util;
import java.io.File;
import java.net.URL;

/**
 * Created by akonopko on 9/30/14.
 */
public class UICommandLine {

    private final URL celosUrl;
    private final URL hueUrl;
    private final int port;

    public UICommandLine(URL celosUrl, URL hueURL, int port) {
        this.celosUrl =  Util.requireNonNull(celosUrl);
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
}
