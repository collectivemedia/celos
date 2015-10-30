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

import com.collective.celos.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Sets;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Renders the UI JSON.
 */
public class ConfigServlet extends HttpServlet {

    private static final String ZOOM_PARAM = "zoom";
    private static final String TIME_PARAM = "time";
    private static final String GROUPS_TAG = "groups";
    private static final String WORKFLOWS_TAG = "workflows";
    private static final String NAME_TAG = "name";
    private static final String UNLISTED_WORKFLOWS_CAPTION = "Unlisted workflows";
    private static final String DEFAULT_CAPTION = "All Workflows";

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter FULL_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm");

    protected static class WorkflowGroupRef {
        public String name;
        public List<Object> rows;
    }

    protected final static ObjectMapper mapper = new ObjectMapper();
    protected final static ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    protected static class ConfigUI {
        public List<WorkflowGroupRef> rows;
    }

    protected static class NavigationPOJO {
        public String left;
        public String right;
        public String zoomIn;
        public String zoomOut;
    }

    private static final int PAGE_SIZE = 20;

    private static NavigationPOJO makeNavigationButtons(ScheduledTime shift, int zoom, ScheduledTime now) throws IOException {
        NavigationPOJO result = new NavigationPOJO();
        // makePaginationButtons
        result.left = shift.minusMinutes(PAGE_SIZE * zoom).toString();
        // right link
        final ScheduledTime tmp = shift.plusMinutes(PAGE_SIZE * zoom);
        if (tmp.compareTo(now) >= 0) {
            result.right = null;
        } else {
            result.right = tmp.toString();
        }
        // makeZoomButtons
        final int last = ZOOM_LEVEL_MINUTES.length - 1;
        final int pos = Math.abs(Arrays.binarySearch(ZOOM_LEVEL_MINUTES, zoom));
        int zoomIn = (0 < pos && pos <= last) ? ZOOM_LEVEL_MINUTES[pos - 1] : ZOOM_LEVEL_MINUTES[0];
        result.zoomIn = Integer.toString(zoomIn);
        int zoomOut = (0 <= pos && pos < last) ? ZOOM_LEVEL_MINUTES[pos + 1] : ZOOM_LEVEL_MINUTES[last];
        result.zoomOut = Integer.toString(zoomOut);
        return result;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            String tmp = processGet(getServletContext(), req.getParameter(TIME_PARAM), req.getParameter(ZOOM_PARAM));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(tmp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // We never want to fetch more data than for a week from Celos so as not to overload the server
    private static int MAX_MINUTES_TO_FETCH = 7 * 60 * 24;
    private static int MAX_TILES_TO_DISPLAY = 48;

    private static final int[] ZOOM_LEVEL_MINUTES = new int[]{1, 5, 15, 30, 60, 60*24};
    static final int DEFAULT_ZOOM_LEVEL_MINUTES = 60;
    static final int MIN_ZOOM_LEVEL_MINUTES = 1;
    static final int MAX_ZOOM_LEVEL_MINUTES = 60*24; // Code won't work with higher level, because of toFullDay()


    public static String processGet(ServletContext servletContext, String timeParam, String zoomParam) throws Exception {
        URL celosURL = (URL) Util.requireNonNull(servletContext.getAttribute(Main.CELOS_URL_ATTR));
        File configFile = (File) servletContext.getAttribute(Main.CONFIG_FILE_ATTR);

        CelosClient client = new CelosClient(celosURL.toURI());
        Set<WorkflowID> workflowIDs = client.getWorkflowList();

        List<WorkflowGroup> groups;

        if (configFile != null) {
            groups = getWorkflowGroups(new FileInputStream(configFile), workflowIDs);
        } else {
            groups = getDefaultGroups(workflowIDs);
        }
        final ConfigUI result = new ConfigUI();
        result.rows = new ArrayList<>();
        for (WorkflowGroup g : groups) {
            final WorkflowGroupRef group = new WorkflowGroupRef();
            group.name = g.getName();
            group.rows = Collections.emptyList();
            result.rows.add(group);
        }
        return writer.writeValueAsString(result);
    }

    static ScheduledTime getDisplayTime(String timeStr, ScheduledTime now) {
        if (timeStr == null || timeStr.isEmpty()) {
            return now;
        } else {
            try {
                return new ScheduledTime(java.net.URLDecoder.decode(timeStr, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return now;
            }
        }
    }
    
    static int getZoomLevel(String zoomStr) {
        if (zoomStr == null || zoomStr.isEmpty()) {
            return DEFAULT_ZOOM_LEVEL_MINUTES;
        } else {
            int zoom = Integer.parseInt(zoomStr);
            if (zoom < MIN_ZOOM_LEVEL_MINUTES) {
                return MIN_ZOOM_LEVEL_MINUTES;
            } else if (zoom > MAX_ZOOM_LEVEL_MINUTES) {
                return MAX_ZOOM_LEVEL_MINUTES;
            } else {
                return zoom;
            }
        }
    }

    static final Map<SlotState.Status, String> STATUS_TO_SHORT_NAME = new HashMap<>();
    static {
        STATUS_TO_SHORT_NAME.put(SlotState.Status.FAILURE, "fail");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.READY, "rdy&nbsp;");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.RUNNING, "run&nbsp;");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.SUCCESS, "&nbsp;&nbsp;&nbsp;&nbsp;");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.WAIT_TIMEOUT, "time");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.WAITING, "wait");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.KILLED, "kill");
        if (STATUS_TO_SHORT_NAME.size() != SlotState.Status.values().length) {
            throw new Error("STATUS_TO_SHORT_NAME mapping is incomplete");
        }
    }




    static List<WorkflowGroup> getWorkflowGroups(InputStream configFileIS, Set<WorkflowID> expectedWfs) throws IOException {
        JsonNode mainNode = objectMapper.readValue(configFileIS, JsonNode.class);
        List<WorkflowGroup> configWorkflowGroups = new ArrayList<>();
        Set<WorkflowID> listedWfs = new TreeSet<>();

        for(JsonNode workflowGroupNode: mainNode.get(GROUPS_TAG)) {
            String[] workflowNames = objectMapper.treeToValue(workflowGroupNode.get(WORKFLOWS_TAG), String[].class);

            List<WorkflowID> ids = new ArrayList<>();
            for (String wfName : workflowNames) {
                ids.add(new WorkflowID(wfName));
            }

            String name = workflowGroupNode.get(NAME_TAG).textValue();
            configWorkflowGroups.add(new WorkflowGroup(name, ids));
            listedWfs.addAll(ids);
        }

        TreeSet<WorkflowID> diff = new TreeSet<>(Sets.difference(expectedWfs, listedWfs));
        if (!diff.isEmpty()) {
            configWorkflowGroups.add(new WorkflowGroup(UNLISTED_WORKFLOWS_CAPTION, new ArrayList<>(diff)));
        }
        return configWorkflowGroups;
    }

    private static List<WorkflowGroup> getDefaultGroups(Set<WorkflowID> workflows) {
        return Collections.singletonList(new WorkflowGroup(DEFAULT_CAPTION, new LinkedList<>(new TreeSet<>(workflows))));
    }

}
