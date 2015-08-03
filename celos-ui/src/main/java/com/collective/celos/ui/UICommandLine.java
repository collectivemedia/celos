package com.collective.celos.ui;

import java.io.File;
import java.net.URL;

/**
 * Created by akonopko on 9/30/14.
 */
public class UICommandLine {

    private final URL celosUrl;
    private final Integer port;

    public UICommandLine(URL celosUrl, Integer port) {
        this.celosUrl = celosUrl;
        this.port = port;
    }

    public URL getCelosUrl() {
        return celosUrl;
    }

    public Integer getPort() {
        return port;
    }
}
