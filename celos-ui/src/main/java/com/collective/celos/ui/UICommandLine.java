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
