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
import com.collective.celos.pojo.Workflow;
import com.collective.celos.pojo.WorkflowGroup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import jdk.nashorn.internal.ir.annotations.Immutable;
import jdk.nashorn.internal.runtime.options.Option;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Called from the browser to rerun a slot.
 */
public class UICommon {

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

    public static final Map<SlotState.Status, String> STATUS_TO_SHORT_NAME = new HashMap<>();
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


    public static Map<Workflow, WorkflowStatus> fetchStatuses(CelosClient client, List<Workflow> workflows, ScheduledTime start, ScheduledTime end) throws Exception {
        Map<Workflow, WorkflowStatus> statuses = new HashMap<>();
        for (Workflow id : workflows) {
            WorkflowStatus status = client.getWorkflowStatus(new WorkflowID(id.getWorkflowName()), start, end);
            statuses.put(id, status);
        }
        return statuses;
    }

    public static Map<ScheduledTime, Set<SlotState>> bucketSlotsByTime(List<SlotState> slotStates, NavigableSet<ScheduledTime> tileTimes) {
        Map<ScheduledTime, Set<SlotState>> buckets = new HashMap<>();
        for (SlotState state : slotStates) {
            ScheduledTime bucketTime = tileTimes.floor(state.getScheduledTime());
            Set<SlotState> slotsForBucket = buckets.get(bucketTime);
            if (slotsForBucket == null) {
                slotsForBucket = new HashSet<>();
                buckets.put(bucketTime, slotsForBucket);
            }
            slotsForBucket.add(state);
        }
        return buckets;
    }

    public static int getNumTiles(int zoomLevelMinutes, int maxMinutesToFetch, int maxTilesToDisplay) {
        return Math.min(maxMinutesToFetch / zoomLevelMinutes, maxTilesToDisplay);
    }

    // Get first tile, e.g. for now=2015-09-01T20:21Z with zoom=5 returns 2015-09-01T20:20Z
    public static ScheduledTime getFirstTileTime(ScheduledTime now, int zoomLevelMinutes) {
        DateTime dt = now.getDateTime();
        DateTime t = Util.toFullDay(dt.plusDays(1));
        while(t.isAfter(dt)) {
            t = t.minusMinutes(zoomLevelMinutes);
        }
        return new ScheduledTime(t);
    }

    public static NavigableSet<ScheduledTime> getTileTimesSet(ScheduledTime firstTileTime, int zoomLevelMinutes, int maxMinutesToFetch, int maxTilesToDisplay) {
        int numTiles = getNumTiles(zoomLevelMinutes, maxMinutesToFetch, maxTilesToDisplay);
        TreeSet<ScheduledTime> times = new TreeSet<>();
        ScheduledTime t = firstTileTime;
        for (int i = 1; i <= numTiles; i++) {
            times.add(t);
            t = t.minusMinutes(zoomLevelMinutes);
        }
        return times;
    }

    public static ScheduledTime getDisplayTime(String timeStr, ScheduledTime now) throws UnsupportedEncodingException {
        if (timeStr == null || timeStr.isEmpty()) {
            return now;
        } else {
            return new ScheduledTime(java.net.URLDecoder.decode(timeStr, "UTF-8"));
        }
    }

    public static int getZoomLevel(String zoomStr) {
        if (zoomStr == null || zoomStr.isEmpty()) {
            return UICommon.DEFAULT_ZOOM_LEVEL_MINUTES;
        } else {
            int zoom = Integer.parseInt(zoomStr);
            if (zoom < UICommon.MIN_ZOOM_LEVEL_MINUTES) {
                return UICommon.MIN_ZOOM_LEVEL_MINUTES;
            } else if (zoom > UICommon.MAX_ZOOM_LEVEL_MINUTES) {
                return UICommon.MAX_ZOOM_LEVEL_MINUTES;
            } else {
                return zoom;
            }
        }
    }


    public static List<WorkflowGroup> getWorkflowGroups(Optional<String> celosConfig, List<Workflow> expectedWfs0) throws IOException {
        final List<Workflow> expectedWfs = expectedWfs0.stream()
                .sorted((a, b) -> a.getWorkflowName().compareTo(b.getWorkflowName()))
                .collect(toList());
        if (!celosConfig.isPresent()) {
            return getDefaultGroups(expectedWfs);
        }
        // else
        JsonNode mainNode = Util.MAPPER.readValue(celosConfig.get(), JsonNode.class);
        List<WorkflowGroup> configWorkflowGroups = new ArrayList<>();

        Set<Workflow> listedWfs = new HashSet<>();
        for(JsonNode workflowGroupNode: mainNode.get(UICommon.GROUPS_TAG)) {
            final JsonNode jsonNode = workflowGroupNode.get(UICommon.WORKFLOWS_TAG);
            final String name = workflowGroupNode.get(UICommon.NAME_TAG).textValue();
            final List<String> workflowNames = Arrays.asList(Util.MAPPER.treeToValue(jsonNode, String[].class));
            final List<Workflow> collect = workflowNames.stream().map(Workflow::new).collect(toList());
            listedWfs.addAll(collect);
            configWorkflowGroups.add(new WorkflowGroup(name).withRows(collect));
        }

        final List<Workflow> collect = expectedWfs.stream()
                .filter(x -> !listedWfs.contains(x))
                .collect(toList());
        if (!collect.isEmpty()) {
            configWorkflowGroups.add(new WorkflowGroup(UICommon.UNLISTED_WORKFLOWS_CAPTION).withRows(collect));
        }
        return configWorkflowGroups;
    }

    public static List<WorkflowGroup> getDefaultGroups(List<Workflow> workflows) {
        return Collections.singletonList(
                new WorkflowGroup(UICommon.DEFAULT_CAPTION).withRows(workflows)
        );
    }

    public static String printTileClass(List<SlotState> slots) {
        if (slots.isEmpty()) {
            return "";
        } else if (slots.size() == 1) {
            return slots.iterator().next().getStatus().name();
        } else {
            return printMultiSlotClass(slots);
        }
    }

    public static String printMultiSlotClass(List<SlotState> slots) {
        boolean hasIndeterminate = false;
        for (SlotState slot : slots) {
            if (slot.getStatus().getType() == SlotState.StatusType.FAILURE) {
                return SlotState.Status.FAILURE.name();
            } else if (slot.getStatus().getType() == SlotState.StatusType.INDETERMINATE) {
                hasIndeterminate = true;
            }
        }
        if (hasIndeterminate) {
            return SlotState.Status.WAITING.name();
        } else {
            return SlotState.Status.SUCCESS.name();
        }
    }

    public static String printWorkflowURL(URL hueURL, SlotState state) {
        return hueURL.toString() + "/list_oozie_workflow/" + state.getExternalID();
    }


}
