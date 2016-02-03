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
import com.collective.celos.pojo.Config;
import com.collective.celos.pojo.Slot;
import com.collective.celos.pojo.WorkflowGroup;
import com.collective.celos.pojo.Workflow;
import com.fasterxml.jackson.databind.JsonNode;
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
 * Renders the full UI JSON.
 * If @group URI parameter is present, returns full state and fetch requested @group data from server.
 * Otherwise returns full state with each workflow = [].
 * format = {rows: [$group1, $group2 ...] }
 */
public class WorkflowGroupsServlet extends HttpServlet {

    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("dd");
    private static final DateTimeFormatter HEADER_FORMAT = DateTimeFormat.forPattern("HHmm");

    // We never want to fetch more data than for a week from Celos so as not to overload the server
    private static final int MAX_MINUTES_TO_FETCH = 7 * 60 * 24;
    private static final int MAX_TILES_TO_DISPLAY = 48;

    private static final Map<SlotState.Status, String> STATUS_TO_SHORT_NAME = new HashMap<>();
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            String tmp = processGet(getServletContext(),
                    req.getParameter(UICommon.TIME_PARAM),
                    req.getParameter(UICommon.ZOOM_PARAM),
                    req.getParameter(UICommon.WF_GROUP_PARAM));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(tmp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected String processGet(ServletContext servletContext, String timeParam, String zoomParam, String wfGroup) throws Exception {
        URL hueURL = (URL) servletContext.getAttribute(Main.HUE_URL_ATTR);
        CelosClient client = UICommon.getCelosClient(servletContext);
        final ScheduledTime now = ScheduledTime.now();
        ScheduledTime timeShift = getDisplayTime(timeParam, now);
        int zoomLevelMinutes = getZoomLevel(zoomParam);
        final NavigableSet<ScheduledTime> tileTimesSet = getTileTimesSet(getFirstTileTime(timeShift, zoomLevelMinutes),
                zoomLevelMinutes,
                MAX_MINUTES_TO_FETCH,
                MAX_TILES_TO_DISPLAY
        );
        ScheduledTime start = tileTimesSet.first();
        Set<WorkflowID> workflowIDs0 = client.getWorkflowList();
        final List<String> workflowIDs = workflowIDs0.stream().map(WorkflowID::toString).collect(toList());

        final Optional<String> celosConfig = UICommon.getCelosConfig(servletContext);

        List<WorkflowGroup> groups = getWorkflowGroups(celosConfig, workflowIDs);
        final List<WorkflowGroup> groupsMatchedName = new ArrayList<>();
        for (WorkflowGroup g : groups) {
            if (g.getName().equals(wfGroup)) {
                final List<WorkflowID> ids = g.getRows().stream()
                        .map(Workflow::toString)
                        .map(WorkflowID::new)
                        .collect(toList());
                final List<WorkflowID> filteredIds = ids.stream().filter(workflowIDs0::contains).collect(toList());
                final Map<WorkflowID, WorkflowStatus> statuses = fetchStatuses(client, filteredIds, start, timeShift);
                final WorkflowGroup res = processWorkflowGroup(g.getName(), ids, tileTimesSet, statuses, hueURL);
                groupsMatchedName.add(res);
            } else {
                groupsMatchedName.add(g);
            }
        }
        final Config config = new Config(groupsMatchedName);
        return Util.JSON_PRETTY.writeValueAsString(config);
    }

    private Slot makeSlot(URL hueURL, List<SlotState> states) {
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
                .limit(UICommon.MULTI_SLOT_INFO_LIMIT)
                .collect(toList()));
        return slot;
    }

    protected WorkflowGroup processWorkflowGroup(String groupName, List<WorkflowID> ids,
                                                 NavigableSet<ScheduledTime> tileTimesSet,
                                                 Map<WorkflowID, WorkflowStatus> statuses,
                                                 URL hueURL) {

        final List<ScheduledTime> tileTimes = tileTimesSet.stream().collect(toList());
        Collections.reverse(tileTimes);
        // Collect times marks
        final List<String> groupTimes = tileTimes.stream()
                .map(tileTime -> HEADER_FORMAT.print(tileTime.getDateTime()))
                .collect(toList());

        // Mark full days as date, half day as "<>"
        final List<String> groupsDays = new ArrayList<>();
        for (ScheduledTime tileTime : tileTimes) {
            if (Util.isFullDay(tileTime.getDateTime())) {
                groupsDays.add(DAY_FORMAT.print(tileTime.getDateTime()));
            } else if (Util.isFullDay(tileTime.plusHours(12).getDateTime())) {
                groupsDays.add("<>");
            } else {
                groupsDays.add(null);
            }
        }
        // collect all table rows
        final List<Workflow> workflows = new ArrayList<>();
        for (WorkflowID id : ids) {
            Workflow workflow = new Workflow(id.toString());
            WorkflowStatus workflowStatus = statuses.get(id);
            if (workflowStatus != null) {
                Map<ScheduledTime, Set<SlotState>> buckets = bucketSlotsByTime(workflowStatus.getSlotStates(), tileTimesSet);
                workflow = workflow.withRows(new ArrayList<>());
                for (ScheduledTime tileTime : tileTimes) {
                    List<SlotState> slots = buckets.getOrDefault(tileTime, new HashSet<>()).stream()
                            .sorted((foo, bar) -> foo.getScheduledTime().compareTo(bar.getScheduledTime()))
                            .collect(toList());
                    workflow.getRows().add(makeSlot(hueURL, slots));
                }
            }
            workflows.add(workflow);
        }
        return new WorkflowGroup(groupName).withRows(workflows).withDays(groupsDays).withTimes(groupTimes);
    }



    private ScheduledTime getDisplayTime(String timeStr, ScheduledTime now) throws UnsupportedEncodingException {
        if (timeStr == null || timeStr.isEmpty()) {
            return now;
        } else {
            return new ScheduledTime(java.net.URLDecoder.decode(timeStr, "UTF-8"));
        }
    }

    private int getZoomLevel(String zoomStr) {
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


    private String printTileClass(List<SlotState> slots) {
        if (slots == null) {
            return "";
        } else if (slots.size() == 1) {
            return slots.iterator().next().getStatus().name();
        } else {
            return printMultiSlotClass(slots);
        }
    }

    private String printMultiSlotClass(List<SlotState> slots) {
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


    private String printWorkflowURL(URL hueURL, SlotState state) {
        return hueURL.toString() + "/list_oozie_workflow/" + state.getExternalID();
    }

    private Map<WorkflowID, WorkflowStatus> fetchStatuses(CelosClient client, List<WorkflowID> workflows, ScheduledTime start, ScheduledTime end) throws Exception {
        Map<WorkflowID, WorkflowStatus> statuses = new HashMap<>();
        for (WorkflowID id : workflows) {
            WorkflowStatus status = client.getWorkflowStatus(id, start, end);
            statuses.put(id, status);
        }
        return statuses;
    }

    private Map<ScheduledTime, Set<SlotState>> bucketSlotsByTime(List<SlotState> slotStates, NavigableSet<ScheduledTime> tileTimes) {
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

    private int getNumTiles(int zoomLevelMinutes, int maxMinutesToFetch, int maxTilesToDisplay) {
        return Math.min(maxMinutesToFetch / zoomLevelMinutes, maxTilesToDisplay);
    }

    // Get first tile, e.g. for now=2015-09-01T20:21Z with zoom=5 returns 2015-09-01T20:20Z
    private ScheduledTime getFirstTileTime(ScheduledTime now, int zoomLevelMinutes) {
        DateTime dt = now.getDateTime();
        DateTime t = Util.toFullDay(dt.plusDays(1));
        while(t.isAfter(dt)) {
            t = t.minusMinutes(zoomLevelMinutes);
        }
        return new ScheduledTime(t);
    }

    private NavigableSet<ScheduledTime> getTileTimesSet(ScheduledTime firstTileTime, int zoomLevelMinutes, int maxMinutesToFetch, int maxTilesToDisplay) {
        int numTiles = getNumTiles(zoomLevelMinutes, maxMinutesToFetch, maxTilesToDisplay);
        TreeSet<ScheduledTime> times = new TreeSet<>();
        ScheduledTime t = firstTileTime;
        for (int i = 1; i <= numTiles; i++) {
            times.add(t);
            t = t.minusMinutes(zoomLevelMinutes);
        }
        return times;
    }

    private List<WorkflowGroup> getWorkflowGroups(Optional<String> celosConfig, List<String> expectedWfs) throws IOException {
        if (!celosConfig.isPresent()) {
            return getDefaultGroups(expectedWfs);
        }
        // else
        JsonNode mainNode = Util.MAPPER.readValue(celosConfig.get(), JsonNode.class);
        List<WorkflowGroup> configWorkflowGroups = new ArrayList<>();
        Set<String> listedWfs = new TreeSet<>();

        for(JsonNode workflowGroupNode: mainNode.get(UICommon.GROUPS_TAG)) {
            String[] workflowNames = Util.MAPPER.treeToValue(workflowGroupNode.get(UICommon.WORKFLOWS_TAG), String[].class);

            String name = workflowGroupNode.get(UICommon.NAME_TAG).textValue();
            final List<Workflow> collect = Arrays.stream(workflowNames)
                    .map(Workflow::new)
                    .collect(toList());
            configWorkflowGroups.add(new WorkflowGroup(name).withRows(collect));
            listedWfs.addAll(Arrays.stream(workflowNames).collect(toSet()));
        }

        final List<Workflow> collect = expectedWfs.stream()
                .filter(listedWfs::contains)
                .map(Workflow::new)
                .collect(toList());
        if (!collect.isEmpty()) {
            configWorkflowGroups.add(new WorkflowGroup(UICommon.UNLISTED_WORKFLOWS_CAPTION)
                    .withRows(collect)
            );
        }
        return configWorkflowGroups;
    }

    private List<WorkflowGroup> getDefaultGroups(List<String> workflows) {
        return Collections.singletonList(
                new WorkflowGroup(UICommon.DEFAULT_CAPTION)
                        .withRows(workflows.stream()
                                .map(Workflow::new)
                                .collect(toList())
                        )
        );
    }

}
