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

    static final int MULTI_SLOT_INFO_LIMIT = 20;

    static final String ZOOM_PARAM = "zoom";
    static final String TIME_PARAM = "time";
    static final String ID_PARAM = "id";
    static final String WF_GROUP_PARAM = "group";
    static final String GROUPS_TAG = "groups";
    static final String WORKFLOWS_TAG = "workflows";
    static final String NAME_TAG = "name";
    static final String UNLISTED_WORKFLOWS_CAPTION = "Unlisted workflows";
    static final String DEFAULT_CAPTION = "All Workflows";

    // We never want to fetch more data than for a week from Celos so as not to overload the server
    static final int MAX_MINUTES_TO_FETCH = 7 * 60 * 24;
    static final int MAX_TILES_TO_DISPLAY = 48;

    static final int DEFAULT_ZOOM_LEVEL_MINUTES = 60;
    static final int MIN_ZOOM_LEVEL_MINUTES = 1;
    static final int MAX_ZOOM_LEVEL_MINUTES = 60*24; // Code won't work with higher level, because of toFullDay()

    static final DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("dd");
    static final DateTimeFormatter HEADER_FORMAT = DateTimeFormat.forPattern("HHmm");
    static final DateTimeFormatter FULL_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm");

    static CelosClient getCelosClient(ServletContext servletContext) throws URISyntaxException {
        URL celosURL = (URL) Util.requireNonNull(servletContext.getAttribute(Main.CELOS_URL_ATTR));
        return new CelosClient(celosURL.toURI());
    }

    static Optional<String> getCelosConfig(ServletContext servletContext) throws IOException {
        final Path configFile = ((File) servletContext.getAttribute(Main.CONFIG_FILE_ATTR)).toPath();
        return  (Files.exists(configFile))
                ? Optional.of(new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8))
                : Optional.empty();
    }

}
