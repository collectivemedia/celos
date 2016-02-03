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

import com.collective.celos.CelosClient;
import com.collective.celos.ScheduledTime;
import com.collective.celos.Util;
import com.collective.celos.WorkflowID;
import com.collective.celos.pojo.Workflow;
import com.collective.celos.pojo.WorkflowGroup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.runtime.options.Option;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Called from the browser to rerun a slot.
 */
class UICommon {

    public static final int MULTI_SLOT_INFO_LIMIT = 20;

    public static final String ZOOM_PARAM = "zoom";
    public static final String TIME_PARAM = "time";
    public static final String ID_PARAM = "id";
    public static final String WF_GROUP_PARAM = "group";
    public static final String GROUPS_TAG = "groups";
    public static final String WORKFLOWS_TAG = "workflows";
    public static final String NAME_TAG = "name";
    public static final String UNLISTED_WORKFLOWS_CAPTION = "Unlisted workflows";
    public static final String DEFAULT_CAPTION = "All Workflows";

    public static final int DEFAULT_ZOOM_LEVEL_MINUTES = 60;
    public static final int MIN_ZOOM_LEVEL_MINUTES = 1;
    public static final int MAX_ZOOM_LEVEL_MINUTES = 60*24; // Code won't work with higher level, because of toFullDay()

    public static CelosClient getCelosClient(ServletContext servletContext) throws URISyntaxException {
        URL celosURL = (URL) Util.requireNonNull(servletContext.getAttribute(Main.CELOS_URL_ATTR));
        return new CelosClient(celosURL.toURI());
    }

    public static Optional<String> getCelosConfig(ServletContext servletContext) throws IOException {
        final Path configFile = ((File) servletContext.getAttribute(Main.CONFIG_FILE_ATTR)).toPath();
        return  (Files.exists(configFile))
                ? Optional.of(new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8))
                : Optional.empty();
    }

}
