package com.collective.celos.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.collect.ImmutableList;

/**
 * Created by akonopko on 22.07.15.
 */
public class CelosUIServlet extends HttpServlet {

    private static final String ZOOM_PARAM = "zoom";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String celosURL = Util.requireNonNull(getServletContext().getAttribute(Main.CELOS_URL_ATTR).toString());
            CelosClient client = new CelosClient(new URI(celosURL));
            res.setContentType("text/html;charset=utf-8");
            res.setStatus(HttpServletResponse.SC_OK);
            PrintWriter writer = res.getWriter();
            writer.append(PREFIX);
            render(client, getZoomLevel(req.getParameter(ZOOM_PARAM)), writer);
            writer.append(POSTFIX);
        } catch (Exception e) {
            throw new ServletException(e);
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

    private static final Map<SlotState.Status, String> STATUS_TO_SHORT_NAME = new HashMap<SlotState.Status, String>();
    static {
        STATUS_TO_SHORT_NAME.put(SlotState.Status.FAILURE, "fail");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.READY, "rdy&nbsp;");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.RUNNING, "run&nbsp;");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.SUCCESS, "&nbsp;&nbsp;&nbsp;&nbsp;");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.WAIT_TIMEOUT, "time");
        STATUS_TO_SHORT_NAME.put(SlotState.Status.WAITING, "wait");
        if (STATUS_TO_SHORT_NAME.size() != SlotState.Status.values().length) {
            throw new Error("STATUS_TO_SHORT_NAME mapping is incomplete");
        }
    }
    
    private static final int[] ZOOM_LEVEL_MINUTES = new int[]{1, 5, 15, 30, 60, 60*24};
    static int DEFAULT_ZOOM_LEVEL_MINUTES = 60;
    static int MIN_ZOOM_LEVEL_MINUTES = 1;
    static int MAX_ZOOM_LEVEL_MINUTES = 60*24; // Code won't work with higher level, because of toFullDay()
    
    // We never want to fetch more data than for a week from Celos so as not to overload the server
    private static int MAX_MINUTES_TO_FETCH = 7 * 60 * 24;
    private static int MAX_TILES_TO_DISPLAY = 48;
    
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("dd");
    private static final DateTimeFormatter HEADER_FORMAT = DateTimeFormat.forPattern("HHmm");
    private static final DateTimeFormatter FULL_FORMAT = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm");
    
    private static String PREFIX;
    static {
        StringBuffer prefix = new StringBuffer();
        prefix.append("<html>\n");
        prefix.append("<head>\n");
        prefix.append("<title>Celos</title>\n");
        prefix.append("<style type='text/css'>\n");
        prefix.append("html { font-family: sans; background-color: #fff; }\n");
        prefix.append("a { text-decoration: none; }\n");
        prefix.append(".mainTable { table-layout: fixed; }\n");
        prefix.append(".workflowGroup { text-align: right; font-size: large; font-weight: bold; padding-top: 1em; padding-right: 20px; }\n");
        prefix.append(".workflow { text-align: right; padding-right: 20px; font-weight: normal; overflow: hidden; }\n");
        prefix.append(".hour, .day, .noDay, .dayHeader { font-family: monospace; text-align: center; }\n");
        prefix.append(".day { background-color: black; color: white; font-weight: bold; }\n");
        prefix.append(".currentDate { font-family: monospace; text-align: right; padding-right: 20px; font-weight: bold; }\n");
        prefix.append(".slot { font-family: monospace; font-size: small; }\n");
        prefix.append(".RUNNING, .READY { background-color: #ffc; }\n");
        prefix.append(".SUCCESS { background-color: #cfc; }\n");
        prefix.append(".WAITING { background-color: #ccf; }\n");
        prefix.append(".FAILURE, .WAIT_TIMEOUT { background-color: #fcc; }\n");
        prefix.append("</style>\n");
        prefix.append("</head>\n");
        prefix.append("<body>\n");
        PREFIX = prefix.toString();
    }
    
    private static String POSTFIX;
    static {
        StringBuffer postfix = new StringBuffer();
        postfix.append("</body>\n");
        postfix.append("</html>\n");
        POSTFIX = postfix.toString();
    }

    private void render(CelosClient client, int zoomLevelMinutes, PrintWriter w) throws Exception {
        Set<WorkflowID> workflows = client.getWorkflowList();
        ScheduledTime now = ScheduledTime.now();
        List<ScheduledTime> times = getDefaultTileTimes(now, zoomLevelMinutes);
        if (times.size() < 1) throw new Error("This shouldn't happen: times list is empty");
        ScheduledTime end = times.get(0);
        // Go zoomLevelMinutes into the past to populate last tile
        ScheduledTime start = times.get(times.size() - 1).minusMinutes(zoomLevelMinutes);
        Map<WorkflowID, WorkflowStatus> statuses = fetchStatuses(client, workflows, start, end);
        Map<WorkflowID, Map<ScheduledTime,Set<SlotState>>> tiles = bucketByTime(statuses, zoomLevelMinutes);
        writeTable(now, getGroups(workflows), times, tiles, w);
    }

    private void writeTable(ScheduledTime now, List<WorkflowGroup> groups, List<ScheduledTime> times, Map<WorkflowID, Map<ScheduledTime, Set<SlotState>>> tiles, PrintWriter w) {
        w.println("<table class='mainTable'>");
        writeHeader(now, times, w);
        for (WorkflowGroup g : groups) {
            writeGroup(g, times, tiles, w);
        }
        w.println("</table>");
    }

    private void writeHeader(ScheduledTime now, List<ScheduledTime> times, PrintWriter w) {
        writeDayHeader(now, times, w);
        writeTimeHeader(now, times, w);
    }
    
    private void writeDayHeader(ScheduledTime now, List<ScheduledTime> times, PrintWriter w) {
        w.println("<tr>");
        // This establishes the width of the workflow IDs column
        // Hack, but enables fast rendering via table-layout: fixed
        w.println("<td class='dayHeader'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>");
        String label, dayClass;
        for (ScheduledTime t : times) {
            if (Util.isFullDay(t.getDateTime())) {
                label = ("&nbsp;" + DAY_FORMAT.print(t.getDateTime()) + "&nbsp;");
                dayClass = "day";
            } else {
                label = "&nbsp;&nbsp;&nbsp;&nbsp;";
                dayClass = "noDay";
            }
            w.println("<td class='" + dayClass + "'>" + label + "</td>");
        }
        w.println("</tr>");
    }

    private void writeTimeHeader(ScheduledTime now, List<ScheduledTime> times, PrintWriter w) {
        w.println("<tr>");
        w.println("<td class='currentDate'>" + FULL_FORMAT.print(now.getDateTime()) + " UTC</td>");
        for (ScheduledTime t : times) {
            w.println("<td class='hour'>" + HEADER_FORMAT.print(t.getDateTime()) + "</td>");
        }
        w.println("</tr>");
    }

    private void writeGroup(WorkflowGroup g, List<ScheduledTime> times, Map<WorkflowID, Map<ScheduledTime, Set<SlotState>>> tiles, PrintWriter w) {
        w.println("<tr>");
        w.println("<td class='workflowGroup'>" + g.getName() + "</td>");
        w.println("</tr>");
        for (WorkflowID id : g.getWorkflows()) {
            writeWorkflow(id, times, tiles.get(id), w);
        }
    }

    private void writeWorkflow(WorkflowID id, List<ScheduledTime> times, Map<ScheduledTime, Set<SlotState>> tiles, PrintWriter w) {
        w.println("<tr>");
        w.println("<td class='workflow'>" + id.toString() + "</td>");
        for (ScheduledTime t : times) {
            Set<SlotState> slots = tiles.get(t);
            String slotClass = "slot " + printTileClass(slots);
            w.println("<td class='" + slotClass + "'>" + printTile(slots) + "</td>");
        }
        w.println("</td>");
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

    private String printTile(Set<SlotState> slots) {
        if (slots == null) {
            return "&nbsp;&nbsp;&nbsp;&nbsp;";
        } else if (slots.size() == 1) {
            return printSingleSlot(slots.iterator().next());
        } else {
            return printMultiSlot(slots.size());
        }
    }

    static String printMultiSlot(int slotsCount) {
        String num = Integer.toString(slotsCount);
        if (num.length() > 4) {
            return "999+";
        } else {
            return num;
        }
    }

    private String printSingleSlot(SlotState next) {
        return STATUS_TO_SHORT_NAME.get(next.getStatus());
    }

    private Map<WorkflowID, Map<ScheduledTime, Set<SlotState>>> bucketByTime(Map<WorkflowID, WorkflowStatus> statuses, int zoomLevelMinutes) {
        Map<WorkflowID, Map<ScheduledTime, Set<SlotState>>> buckets = new HashMap<>();
        for (Map.Entry<WorkflowID, WorkflowStatus> status : statuses.entrySet()) {
            buckets.put(status.getKey(), bucketSlotsByTime(status.getValue().getSlotStates(), zoomLevelMinutes));
        }
        return buckets;
    }

    static Map<ScheduledTime, Set<SlotState>> bucketSlotsByTime(List<SlotState> slotStates, int zoomLevelMinutes) {
        Map<ScheduledTime, Set<SlotState>> buckets = new HashMap<>();
        for (SlotState slot : slotStates) {
            ScheduledTime bucketTime = bucketTime(slot.getScheduledTime(), zoomLevelMinutes);
            Set<SlotState> slotsForBucket = buckets.get(bucketTime);
            if (slotsForBucket == null) {
                slotsForBucket = new HashSet<>();
                buckets.put(bucketTime, slotsForBucket);
            }
            slotsForBucket.add(slot);
        }
        return buckets;
    }

    private Map<WorkflowID, WorkflowStatus> fetchStatuses(CelosClient client, Set<WorkflowID> workflows, ScheduledTime start, ScheduledTime end) throws Exception {
        Map<WorkflowID, WorkflowStatus> statuses = new HashMap<>();
        for (WorkflowID id : workflows) {
            WorkflowStatus status = client.getWorkflowStatus(id, start, end);
            statuses.put(id, status);
        }
        return statuses;
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

    private List<WorkflowGroup> getGroups(Set<WorkflowID> workflows) {
        // For now, stuff all workflows into a single group
        return ImmutableList.of(new WorkflowGroup("All Workflows", new LinkedList<WorkflowID>(new TreeSet<WorkflowID>(workflows))));
    }
    
}
