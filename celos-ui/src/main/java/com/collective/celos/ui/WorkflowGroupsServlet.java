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
import com.collective.celos.pojo.MainStore;
import com.collective.celos.pojo.Tile;
import com.collective.celos.pojo.WorkflowGroup;
import com.collective.celos.pojo.Workflow;
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

import static com.collective.celos.ui.UICommon.getWorkflowGroups;
import static com.collective.celos.ui.UICommon.printWorkflowURL;
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
        ScheduledTime timeShift = UICommon.getDisplayTime(timeParam, now);
        int zoomLevelMinutes = UICommon.getZoomLevel(zoomParam);
        final NavigableSet<ScheduledTime> tileTimesSet = UICommon.getTileTimesSet(UICommon.getFirstTileTime(timeShift, zoomLevelMinutes),
                zoomLevelMinutes,
                MAX_MINUTES_TO_FETCH,
                MAX_TILES_TO_DISPLAY
        );
        final ScheduledTime start = tileTimesSet.first();
        final List<Workflow> workflows = client.getWorkflowList().stream()
                .map(WorkflowID::toString)
                .sorted()
                .map(Workflow::new)
                .collect(toList());
        final Optional<String> celosConfig = UICommon.getCelosConfig(servletContext);
        final List<WorkflowGroup> groups = getWorkflowGroups(celosConfig, workflows);
        final Set<Workflow> workflowSet = workflows.stream().collect(toSet());
        final List<WorkflowGroup> groupsMatchedName = new ArrayList<>();
        for (WorkflowGroup g : groups) {
            if (g.getName().equals(wfGroup)) {
                final List<Workflow> ids = g.getRows();
                final List<Workflow> filteredIds = ids.stream().filter(workflowSet::contains).collect(toList());
                final Map<Workflow, WorkflowStatus> statuses = UICommon.fetchStatuses(client, filteredIds, start, timeShift);
                final WorkflowGroup res = processWorkflowGroup(g.getName(), ids, tileTimesSet, statuses, hueURL);
                groupsMatchedName.add(res);
            } else {
                groupsMatchedName.add(g);
            }
        }
        final MainStore config = new MainStore(groupsMatchedName);
        return Util.JSON_PRETTY.writeValueAsString(config);
    }

    private Tile makeTile(URL hueURL, List<SlotState> states) {
        if (states.isEmpty()) {
            return new Tile("EMPTY").withQuantity(0);
        }
        Tile slot = new Tile(UICommon.printTileClass(states));
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

    protected WorkflowGroup processWorkflowGroup(String groupName, List<Workflow> ids,
                                                 NavigableSet<ScheduledTime> tileTimesSet,
                                                 Map<Workflow, WorkflowStatus> statuses,
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
        for (final Workflow workflow : ids) {
            WorkflowStatus workflowStatus = statuses.get(workflow);
            final ArrayList<Tile> tiles = new ArrayList<>();
            if (workflowStatus != null) {
                Map<ScheduledTime, Set<SlotState>> buckets = UICommon.bucketSlotsByTime(workflowStatus.getSlotStates(), tileTimesSet);
                for (ScheduledTime tileTime : tileTimes) {
                    List<SlotState> slots = buckets.getOrDefault(tileTime, new HashSet<>()).stream()
                            .sorted((foo, bar) -> foo.getScheduledTime().compareTo(bar.getScheduledTime()))
                            .collect(toList());
                    tiles.add(makeTile(hueURL, slots));
                }
            }
            workflows.add(workflow.withRows(tiles));
        }
        return new WorkflowGroup(groupName).withRows(workflows).withDays(groupsDays).withTimes(groupTimes);
    }

}
