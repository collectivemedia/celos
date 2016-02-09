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
package com.collective.celos.old;

import static com.collective.celos.ui.UICommon.*;
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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.collective.celos.*;
import com.collective.celos.pojo.Workflow;
import com.collective.celos.pojo.WorkflowGroup;
import com.collective.celos.ui.Main;
import com.collective.celos.ui.UICommon;
import j2html.tags.Tag;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            URL celosURL = (URL) Util.requireNonNull(getServletContext().getAttribute(Main.CELOS_URL_ATTR));
            URL hueURL = (URL) getServletContext().getAttribute(Main.HUE_URL_ATTR);
            File configFile = (File) getServletContext().getAttribute(Main.CONFIG_FILE_ATTR);
            final CelosClient client = new CelosClient(celosURL.toURI());
            res.setContentType("text/html;charset=utf-8");
            res.setStatus(HttpServletResponse.SC_OK);
            final ScheduledTime end = getDisplayTime(req.getParameter(TIME_PARAM));
            int zoomLevelMinutes = getZoomLevel(req.getParameter(ZOOM_PARAM));
            final NavigableSet<ScheduledTime> tileTimes = getTileTimesSet(getFirstTileTime(end, zoomLevelMinutes), zoomLevelMinutes, MAX_MINUTES_TO_FETCH, MAX_TILES_TO_DISPLAY);
            final ScheduledTime start = tileTimes.first();
            final List<Workflow> workflows = client.getWorkflowList().stream().map(WorkflowID::toString).sorted().map(Workflow::new).collect(toList());
            final Map<Workflow, WorkflowStatus> statuses = fetchStatuses(client, workflows, start, end);
            final Optional<String> celosConfig = UICommon.getCelosConfig(getServletContext());
            final List<WorkflowGroup> groups = getWorkflowGroups(celosConfig, workflows);
            final UIConfiguration conf = new UIConfiguration(start, end, tileTimes, groups, statuses, hueURL);
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
        for (Workflow id : g.getRows()) {
            rows.add(makeWorkflowRow(conf, id));
        }
        return rows;
    }

    private static Tag makeWorkflowRow(UIConfiguration conf, Workflow id) {
        WorkflowStatus workflowStatus = conf.getStatuses().get(id);
        if (workflowStatus == null) {
            return tr().with(td(id.getWorkflowName() + " (missing)").withClass("workflow missing"));
        }
        Map<ScheduledTime, Set<SlotState>> buckets = bucketSlotsByTime(workflowStatus.getSlotStates(), conf.getTileTimes());
        List<Tag> cells = new LinkedList<>();
        cells.add(td(id.getWorkflowName()).withClass("workflow"));
        for (ScheduledTime tileTime : conf.getTileTimes().descendingSet()) {
            Set<SlotState> slots = buckets.get(tileTime);
            String slotClass = "slot " + printTileClass(slots.stream().collect(toList()));
            cells.add(td().with(makeTile(conf, slots)).withClass(slotClass));
        }
        return tr().with(cells);
    }

    private static Tag makeTile(UIConfiguration conf, Set<SlotState> slots) {
        if (slots == null) {
            return unsafeHtml("&nbsp;&nbsp;&nbsp;&nbsp;");
        } else if (slots.size() == 1) {
            return makeSingleSlot(conf, slots.iterator().next());
        } else {
            return makeMultiSlot(slots.size());
        }
    }

    static Tag makeMultiSlot(int slotsCount) {
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
            return a().withHref(printWorkflowURL(conf.getHueURL(), state))
                      .withClass("slotLink")
                      .attr("data-slot-id", state.getSlotID().toString())
                      .with(label);
        } else {
            return label;
        }
    }

}
