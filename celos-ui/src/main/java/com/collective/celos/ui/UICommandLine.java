package com.collective.celos.ui;

import com.collective.celos.Util;

import java.io.File;
import java.net.URL;

/**
 * Created by akonopko on 9/30/14.
 */
public class UICommandLine {

    private final URL celosUrl;
    private final int port;

    public UICommandLine(URL celosUrl, int port) {
        this.celosUrl =  Util.requireNonNull(celosUrl);
        this.port = port;
    }

    public URL getCelosUrl() {
        return celosUrl;
    }

    public int getPort() {
        return port;
    }
}
