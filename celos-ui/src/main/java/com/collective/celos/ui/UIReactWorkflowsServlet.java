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
import com.collective.celos.pojo.Slot;
import com.collective.celos.pojo.WorkflowGroup;
import com.collective.celos.pojo.Workflow;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Renders the UI JSON.
 */
public class UIReactWorkflowsServlet extends HttpServlet {

    private static final String ZOOM_PARAM = "zoom";
    private static final String TIME_PARAM = "time";
    private static final String WF_GROUP_PARAM = "group";
    private static final String GROUPS_TAG = "groups";
    private static final String WORKFLOWS_TAG = "workflows";
    private static final String NAME_TAG = "name";
    private static final String UNLISTED_WORKFLOWS_CAPTION = "Unlisted workflows";
    private static final String DEFAULT_CAPTION = "All Workflows";

    private static Slot makeSlot(URL hueURL, List<SlotState> states) {
        if (states.isEmpty()) {
            return new Slot("EMPTY").withQuantity(0);
        }
        Slot slot = new Slot(printTileClass(states));
        if (states.size() == 1 && hueURL != null && states.get(0).getExternalID() != null) {
            slot = slot.withUrl(printWorkflowURL(hueURL, states.get(0)));
        }
        slot = slot.withQuantity(states.size());
        slot = slot.withTimestamps(states.stream()
                .map(x -> ZonedDateTime.parse(x.getScheduledTime().toString()).toString())
                .limit(Main.MULTI_SLOT_INFO_LIMIT)
                .collect(toList()));
        return slot;
    }

    protected static WorkflowGroup processWorkflowGroup(String name, List<WorkflowID> ids, NavigableSet<ScheduledTime> tileTimesSet, Map<WorkflowID, WorkflowStatus> statuses, URL hueURL) {

        WorkflowGroup group = new WorkflowGroup(name);
        final List<ScheduledTime> tileTimes = tileTimesSet.stream().collect(toList());
        Collections.reverse(tileTimes);
        group = group.withTimes(tileTimes.stream()
                .map(tileTime -> HEADER_FORMAT.print(tileTime.getDateTime()))
                .collect(toList()));

        // Mark full days as date, half day as "<>"
        group = group.withDays(tileTimes.stream()
                .map(tileTime ->
                        (Util.isFullDay(tileTime.getDateTime()))
                                ? DAY_FORMAT.print(tileTime.getDateTime())
                                : (Util.isFullDay(tileTime.plusHours(12).getDateTime()))
                                ? "<>"
                                : null)
                .collect(toList()));

        group = group.withRows(new ArrayList<>());
        for (WorkflowID id : ids) {
            Workflow workflow = new Workflow(id.toString());
            group.getRows().add(workflow);
            WorkflowStatus workflowStatus = statuses.get(id);
            if (workflowStatus == null) {
                continue;
            }
            Map<ScheduledTime, Set<SlotState>> buckets = bucketSlotsByTime(workflowStatus.getSlotStates(), tileTimesSet);
            workflow = workflow.withRows(new ArrayList<>());
            for (ScheduledTime tileTime : tileTimes) {
                List<SlotState> slots = buckets.getOrDefault(tileTime, new HashSet<>()).stream()
                        .sorted((foo, bar) -> foo.getScheduledTime().compareTo(bar.getScheduledTime()))
                        .collect(toList());
                workflow.getRows().add(makeSlot(hueURL, slots));
            }
        }

        return group;
    }


    // We never want to fetch more data than for a week from Celos so as not to overload the server
    private static int MAX_MINUTES_TO_FETCH = 7 * 60 * 24;
    private static int MAX_TILES_TO_DISPLAY = 48;

    static final int DEFAULT_ZOOM_LEVEL_MINUTES = 60;
    static final int MIN_ZOOM_LEVEL_MINUTES = 1;
    static final int MAX_ZOOM_LEVEL_MINUTES = 60*24; // Code won't work with higher level, because of toFullDay()


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            String tmp = processGet(getServletContext(),
                    req.getParameter(TIME_PARAM),
                    req.getParameter(ZOOM_PARAM),
                    req.getParameter(WF_GROUP_PARAM));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(tmp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }


    static List<WorkflowGroup> getWorkflowGroups(InputStream configFileIS, Set<WorkflowID> expectedWfs) throws IOException {
        JsonNode mainNode = Util.MAPPER.readValue(configFileIS, JsonNode.class);
        List<WorkflowGroup> configWorkflowGroups = new ArrayList<>();
        Set<String> listedWfs = new TreeSet<>();

        for(JsonNode workflowGroupNode: mainNode.get(GROUPS_TAG)) {
            String[] workflowNames = Util.MAPPER.treeToValue(workflowGroupNode.get(WORKFLOWS_TAG), String[].class);

            String name = workflowGroupNode.get(NAME_TAG).textValue();
            final List<Workflow> collect = Arrays.stream(workflowNames)
                    .map(Workflow::new)
                    .collect(toList());
            configWorkflowGroups.add(new WorkflowGroup(name).withRows(collect));
            listedWfs.addAll(Arrays.stream(workflowNames).collect(toSet()));
        }

        TreeSet<WorkflowID> diff = new TreeSet<>(Sets.difference(expectedWfs, listedWfs));
        if (!diff.isEmpty()) {
            final List<Workflow> collect = diff.stream()
                    .map(x -> new Workflow(x.toString()))
                    .collect(toList());
            configWorkflowGroups.add(new WorkflowGroup(UNLISTED_WORKFLOWS_CAPTION).withRows(collect));
        }
        return configWorkflowGroups;
    }

    public static String processGet(ServletContext servletContext, String timeParam, String zoomParam, String wfGroup) throws Exception {
        URL hueURL = (URL) servletContext.getAttribute(Main.HUE_URL_ATTR);
        File configFile = (File) servletContext.getAttribute(Main.CONFIG_FILE_ATTR);
        CelosClient client = Main.getCelosClient(servletContext);
        final ScheduledTime now = ScheduledTime.now();
        ScheduledTime timeShift = getDisplayTime(timeParam, now);
        int zoomLevelMinutes = getZoomLevel(zoomParam);
        final NavigableSet<ScheduledTime> tileTimesSet = getTileTimesSet(getFirstTileTime(timeShift, zoomLevelMinutes), zoomLevelMinutes, MAX_MINUTES_TO_FETCH, MAX_TILES_TO_DISPLAY);
        ScheduledTime start = tileTimesSet.first();
        Set<WorkflowID> workflowIDs = client.getWorkflowList();

        List<WorkflowGroup> groups;
        if (configFile != null) {
            groups = getWorkflowGroups(new FileInputStream(configFile), workflowIDs);
        } else {
            groups = getDefaultGroups(workflowIDs);
        }
        final List<WorkflowGroup> groupsMatchedName = groups.stream()
                .filter(u -> u.getName().equals(wfGroup))
                .collect(toList());
        if (groupsMatchedName.isEmpty()) {
            throw new Exception("group not found");
        }
        final WorkflowGroup workflowGroup = groupsMatchedName.get(0);

        final List<WorkflowID> ids = workflowIDs.stream()
                .filter(workflowGroup.getRows()::contains)
                .collect(toList());

        Map<WorkflowID, WorkflowStatus> statuses = fetchStatuses(client, ids, start, timeShift);

        final WorkflowGroup groupData = processWorkflowGroup(workflowGroup.getName(), ids, tileTimesSet, statuses, hueURL);

        return Util.JSON_PRETTY.writeValueAsString(groupData);
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


    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("dd");
    private static final DateTimeFormatter HEADER_FORMAT = DateTimeFormat.forPattern("HHmm");
    private static final DateTimeFormatter FULL_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm");
    

    static String printTileClass(List<SlotState> slots) {
        if (slots == null) {
            return "";
        } else if (slots.size() == 1) {
            return slots.iterator().next().getStatus().name();
        } else {
            return printMultiSlotClass(slots);
        }
    }

    static String printMultiSlotClass(List<SlotState> slots) {
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


    private static String printWorkflowURL(URL hueURL, SlotState state) {
        return hueURL.toString() + "/list_oozie_workflow/" + state.getExternalID();
    }

    private static Map<WorkflowID, WorkflowStatus> fetchStatuses(CelosClient client, List<WorkflowID> workflows, ScheduledTime start, ScheduledTime end) throws Exception {
        Map<WorkflowID, WorkflowStatus> statuses = new HashMap<>();
        for (WorkflowID id : workflows) {
            WorkflowStatus status = client.getWorkflowStatus(id, start, end);
            statuses.put(id, status);
        }
        return statuses;
    }

    static Map<ScheduledTime, Set<SlotState>> bucketSlotsByTime(List<SlotState> slotStates, NavigableSet<ScheduledTime> tileTimes) {
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
    
    static int getNumTiles(int zoomLevelMinutes, int maxMinutesToFetch, int maxTilesToDisplay) {
        return Math.min(maxMinutesToFetch / zoomLevelMinutes, maxTilesToDisplay);
    }


    private static DateTime toFullDay(DateTime dt) {
        return dt.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
    }
    
    // Get first tile, e.g. for now=2015-09-01T20:21Z with zoom=5 returns 2015-09-01T20:20Z
    static ScheduledTime getFirstTileTime(ScheduledTime now, int zoomLevelMinutes) {
        DateTime dt = now.getDateTime();
        DateTime t = toFullDay(dt.plusDays(1));
        while(t.isAfter(dt)) {
            t = t.minusMinutes(zoomLevelMinutes);
        }
        return new ScheduledTime(t);
    }
    
    static NavigableSet<ScheduledTime> getTileTimesSet(ScheduledTime firstTileTime, int zoomLevelMinutes, int maxMinutesToFetch, int maxTilesToDisplay) {
        int numTiles = getNumTiles(zoomLevelMinutes, maxMinutesToFetch, maxTilesToDisplay);
        TreeSet<ScheduledTime> times = new TreeSet<>();
        ScheduledTime t = firstTileTime;
        for (int i = 1; i <= numTiles; i++) {
            times.add(t);
            t = t.minusMinutes(zoomLevelMinutes);
        }
        return times;
    }

    private static List<WorkflowGroup> getDefaultGroups(Set<WorkflowID> workflows) {
        final List<Workflow> collect = workflows.stream().map(x -> new Workflow(x.toString())).collect(toList());
        return Collections.singletonList(new WorkflowGroup(DEFAULT_CAPTION).withRows(collect));
    }

}
