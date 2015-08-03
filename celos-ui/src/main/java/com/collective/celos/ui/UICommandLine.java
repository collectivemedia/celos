package com.collective.celos.ui;

import java.io.File;
import java.net.URL;

import com.collective.celos.Util;

/**
 * Created by akonopko on 9/30/14.
 */
public class UICommandLine {

    private final URL celosUrl;
    private final Integer port;

    public UICommandLine(URL celosUrl, Integer port) {
        this.celosUrl = Util.requireNonNull(celosUrl);
        this.port = Util.requireNonNull(port);
    }

    public URL getCelosUrl() {
        return celosUrl;
    }

    public Integer getPort() {
        return port;
    }
}
