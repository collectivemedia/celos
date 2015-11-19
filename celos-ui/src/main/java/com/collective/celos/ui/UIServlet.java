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

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.text;
import static j2html.TagCreator.link;
import static j2html.TagCreator.script;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.title;
import static j2html.TagCreator.tr;
import static j2html.TagCreator.unsafeHtml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import j2html.tags.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.collective.celos.CelosClient;
import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotState;
import com.collective.celos.Util;
import com.collective.celos.WorkflowID;
import com.collective.celos.WorkflowStatus;
import com.google.common.collect.ImmutableList;

/**
 * Renders the UI HTML.
 */
public class UIServlet extends HttpServlet {

    private static final String ZOOM_PARAM = "zoom";
    private static final String TIME_PARAM = "time";
    private static final String GROUPS_TAG = "groups";
    private static final String WORKFLOWS_TAG = "workflows";
    private static final String NAME_TAG = "name";
    private static final String UNLISTED_WORKFLOWS_CAPTION = "Unlisted workflows";
    private static final String DEFAULT_CAPTION = "All Workflows";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            URL celosURL = (URL) Util.requireNonNull(getServletContext().getAttribute(Main.CELOS_URL_ATTR));
            URL hueURL = (URL) getServletContext().getAttribute(Main.HUE_URL_ATTR);
            File configFile = (File) getServletContext().getAttribute(Main.CONFIG_FILE_ATTR);

            CelosClient client = new CelosClient(celosURL.toURI());
            res.setContentType("text/html;charset=utf-8");
            res.setStatus(HttpServletResponse.SC_OK);
            ScheduledTime end = getDisplayTime(req.getParameter(TIME_PARAM));
            int zoomLevelMinutes = getZoomLevel(req.getParameter(ZOOM_PARAM));
            NavigableSet<ScheduledTime> tileTimes = getTileTimesSet(getFirstTileTime(end, zoomLevelMinutes), zoomLevelMinutes, MAX_MINUTES_TO_FETCH, MAX_TILES_TO_DISPLAY);
            ScheduledTime start = tileTimes.first();
            Set<WorkflowID> workflowIDs = client.getWorkflowList();

            Map<WorkflowID, WorkflowStatus> statuses = fetchStatuses(client, workflowIDs, start, end);

            List<WorkflowGroup> groups;

            if (configFile != null) {
                groups = getWorkflowGroups(new FileInputStream(configFile), workflowIDs);
            } else {
                groups = getDefaultGroups(workflowIDs);
            }

            UIConfiguration conf = new UIConfiguration(start, end, tileTimes, groups, statuses, hueURL);
            res.getWriter().append(render(conf));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    static ScheduledTime getDisplayTime(String timeStr) {
        if (timeStr == null) {
            return ScheduledTime.now();
        } else {
            return new ScheduledTime(timeStr);
        }
    }
    
    static int getZoomLevel(String zoomStr) {
        if (zoomStr == null) {
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

    static final Map<SlotState.Status, String> STATUS_TO_SHORT_NAME = new HashMap<SlotState.Status, String>();
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
    
    private static final int[] ZOOM_LEVEL_MINUTES = new int[]{1, 5, 15, 30, 60, 60*24};
    static final int DEFAULT_ZOOM_LEVEL_MINUTES = 60;
    static final int MIN_ZOOM_LEVEL_MINUTES = 1;
    static final int MAX_ZOOM_LEVEL_MINUTES = 60*24; // Code won't work with higher level, because of toFullDay()
    
    // We never want to fetch more data than for a week from Celos so as not to overload the server
    private static int MAX_MINUTES_TO_FETCH = 7 * 60 * 24;
    private static int MAX_TILES_TO_DISPLAY = 48;
    
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("dd");
    private static final DateTimeFormatter HEADER_FORMAT = DateTimeFormat.forPattern("HHmm");
    private static final DateTimeFormatter FULL_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm");
    
    static String render(UIConfiguration conf) throws Exception {
        return html().with(makeHead(), makeBody(conf)).render();
    }

    private static Tag makeHead() {
        return head().with(title("Celos"),
                           link().withType("text/css").withRel("stylesheet").withHref("/static/style.css"),
                           script().withType("text/javascript").withSrc("/static/jquery.min.js"),
                           script().withType("text/javascript").withSrc("/static/script.js"));
    }

    private static Tag makeBody(UIConfiguration conf) {
        return body().with(makeTable(conf), div().with(text("(Shift-click a slot to rerun it.)")));
    }

    private static Tag makeTable(UIConfiguration conf) {
        List<Tag> contents = new LinkedList<>();
        contents.addAll(makeTableHeader(conf));
        contents.addAll(makeTableRows(conf));
        return table().withClass("mainTable").with(contents);
    }

    private static List<Tag> makeTableHeader(UIConfiguration conf) {
        return ImmutableList.of(makeDayHeader(conf), makeTimeHeader(conf));
    }

    private static Tag makeDayHeader(UIConfiguration conf) {
        List<Tag> cells = new LinkedList<>();
        cells.add(td().with(unsafeHtml("&nbsp;")));
        for (ScheduledTime time : conf.getTileTimes().descendingSet()) {
            cells.add(makeDay(time));
        }
        return tr().with(cells);
    }

    private static Tag makeDay(ScheduledTime time) {
        if (Util.isFullDay(time.getDateTime())) {
            return td().with(unsafeHtml("&nbsp;" + DAY_FORMAT.print(time.getDateTime()) + "&nbsp;")).withClass("day");
        } else {
            return td().with(unsafeHtml("&nbsp;&nbsp;&nbsp;&nbsp;")).withClass("noDay");
        }
    }
    
    private static Tag makeTimeHeader(UIConfiguration conf) {
        List<Tag> cells = new LinkedList<>();
        cells.add(td(FULL_FORMAT.print(conf.getEnd().getDateTime()) + " UTC").withClass("currentDate"));
        for (ScheduledTime time : conf.getTileTimes().descendingSet()) {
            cells.add(makeHour(time));
        }
        return tr().with(cells);
    }
    
    private static Tag makeHour(ScheduledTime time) {
        return td(HEADER_FORMAT.print(time.getDateTime())).withClass("hour");
    }
    
    private static List<Tag> makeTableRows(UIConfiguration conf) {
        List<Tag> rows = new LinkedList<>();
        for (WorkflowGroup g : conf.getGroups()) {
            rows.addAll(makeGroupRows(conf, g));
        }
        return rows;
    }

    private static List<Tag> makeGroupRows(UIConfiguration conf, WorkflowGroup g) {
        List<Tag> rows = new LinkedList<>();
        rows.add(tr().with(td(g.getName()).withClass("workflowGroup")));
        for (WorkflowID id : g.getWorkflows()) {
            rows.add(makeWorkflowRow(conf, id));
        }
        return rows;
    }

    private static Tag makeWorkflowRow(UIConfiguration conf, WorkflowID id) {
        WorkflowStatus workflowStatus = conf.getStatuses().get(id);
        if (workflowStatus == null) {
            return tr().with(td(id.toString() + " (missing)").withClass("workflow missing"));
        }
        Map<ScheduledTime, Set<SlotState>> buckets = bucketSlotsByTime(workflowStatus.getSlotStates(), conf.getTileTimes());
        List<Tag> cells = new LinkedList<>();
        cells.add(td(id.toString()).withClass("workflow"));
        for (ScheduledTime tileTime : conf.getTileTimes().descendingSet()) {
            Set<SlotState> slots = buckets.get(tileTime);
            String slotClass = "slot " + printTileClass(slots);
            cells.add(td().with(makeTile(conf, slots)).withClass(slotClass));
        }
        return tr().with(cells);
    }

    static String printTileClass(Set<SlotState> slots) {
        if (slots == null) {
            return "";
        } else if (slots.size() == 1) {
            return slots.iterator().next().getStatus().name();
        } else {
            return printMultiSlotClass(slots);
        }
    }

    static String printMultiSlotClass(Set<SlotState> slots) {
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

    private static Tag makeTile(UIConfiguration conf, Set<SlotState> slots) {
        if (slots == null) {
            return unsafeHtml("&nbsp;&nbsp;&nbsp;&nbsp;");
        } else if (slots.size() == 1) {
            return makeSingleSlot(conf, slots.iterator().next());
        } else {
            return makeMultiSlot(conf, slots.size());
        }
    }

    static Tag makeMultiSlot(UIConfiguration conf, int slotsCount) {
        String num = Integer.toString(slotsCount);
        if (num.length() > 4) {
            return unsafeHtml("999+");
        } else {
            return unsafeHtml(num);
        }
    }

    private static Tag makeSingleSlot(UIConfiguration conf, SlotState state) {
        Tag label = unsafeHtml(STATUS_TO_SHORT_NAME.get(state.getStatus()));
        if (conf.getHueURL() != null && state.getExternalID() != null) {
            return a().withHref(printWorkflowURL(conf, state)).withClass("slotLink").attr("data-slot-id", state.getSlotID().toString()).with(label);
        } else {
            return label;
        }
    }

    private static String printWorkflowURL(UIConfiguration conf, SlotState state) {
        return conf.getHueURL().toString() + "/list_oozie_workflow/" + state.getExternalID();
    }

    private Map<WorkflowID, WorkflowStatus> fetchStatuses(CelosClient client, Set<WorkflowID> workflows, ScheduledTime start, ScheduledTime end) throws Exception {
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
    
    static List<ScheduledTime> getDefaultTileTimes(ScheduledTime now, int zoomLevelMinutes) {
        return getTileTimes(now, zoomLevelMinutes, MAX_MINUTES_TO_FETCH, MAX_TILES_TO_DISPLAY);
    }
    
    static List<ScheduledTime> getTileTimes(ScheduledTime now, int zoomLevelMinutes, int maxMinutesToFetch, int maxTilesToDisplay) {
        int numTiles = getNumTiles(zoomLevelMinutes, maxMinutesToFetch, maxTilesToDisplay);
        List<ScheduledTime> times = new LinkedList<>();
        ScheduledTime t = now;
        for (int i = 1; i <= numTiles; i++) {
            times.add(bucketTime(t, zoomLevelMinutes));
            t = t.minusMinutes(zoomLevelMinutes);
        }
        return times;
    }

    static int getNumTiles(int zoomLevelMinutes, int maxMinutesToFetch, int maxTilesToDisplay) {
        return Math.min(maxMinutesToFetch / zoomLevelMinutes, maxTilesToDisplay);
    }

    static ScheduledTime bucketTime(ScheduledTime t, int zoomLevelMinutes) {
        DateTime dtNow = t.getDateTime();
        DateTime dtFullDay = toFullDay(dtNow);
        DateTime dt = dtFullDay;
        while(dt.isBefore(dtNow)) {
            dt = dt.plusMinutes(zoomLevelMinutes);
        }
        return new ScheduledTime(dt);
    }

    private static DateTime toFullDay(DateTime dt) {
        return dt.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
    }
    
    // Get first tile, e.g. for now=2015-09-01T20:21Z with zoom=5 returns 2015-09-01T20:20Z
    static ScheduledTime getFirstTileTime(ScheduledTime now, int zoomLevelMinutes) {
        DateTime dt = now.getDateTime();
        DateTime nextDay = toFullDay(dt.plusDays(1));
        DateTime t = nextDay;
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

    List<WorkflowGroup> getWorkflowGroups(InputStream configFileIS, Set<WorkflowID> expectedWfs) throws IOException {
        JsonNode mainNode = Util.JSON_READER.withType(JsonNode.class).readValue(configFileIS);
        List<WorkflowGroup> configWorkflowGroups = new ArrayList();
        Set<WorkflowID> listedWfs = new TreeSet<>();

        for(JsonNode workflowGroupNode: mainNode.get(GROUPS_TAG)) {
            String[] workflowNames = Util.JSON_READER.treeToValue(workflowGroupNode.get(WORKFLOWS_TAG), String[].class);

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

    private List<WorkflowGroup> getDefaultGroups(Set<WorkflowID> workflows) {
        return ImmutableList.of(new WorkflowGroup(DEFAULT_CAPTION, new LinkedList<WorkflowID>(new TreeSet<WorkflowID>(workflows))));
    }

}
