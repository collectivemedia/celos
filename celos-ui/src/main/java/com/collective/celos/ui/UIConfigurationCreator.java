package com.collective.celos.ui;

import com.collective.celos.ScheduledTime;
import com.collective.celos.WorkflowID;
import com.collective.celos.WorkflowStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class UIConfigurationCreator {

    public static final String HUE_URL_TAG = "defaultHueURL";
    public static final String WORKFLOW_TO_SLOT_MAP_TAG = "workflowToSlotMap";
    public static final String WORKFLOWS_TAG = "workflows";
    public static final String NAME_TAG = "name";
    public static final String UNLISTED_WORKFLOWS_CAPTION = "Unlisted workflows";
    public static final String DEFAULT_CAPTION = "All Workflows";

    private final File configFile;
    private final ScheduledTime end;
    private final NavigableSet<ScheduledTime> tileTimes;
    private final ScheduledTime start;
    private final Set<WorkflowID> workflowIDs;
    private final Map<WorkflowID, WorkflowStatus> statuses;
    private final URL defaultHueURL;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UIConfigurationCreator(URL defaultHueURL, File configFile, ScheduledTime end, NavigableSet<ScheduledTime> tileTimes, ScheduledTime start, Set<WorkflowID> workflowIDs, Map<WorkflowID, WorkflowStatus> statuses) {
        this.defaultHueURL = defaultHueURL;
        this.configFile = configFile;
        this.end = end;
        this.tileTimes = tileTimes;
        this.start = start;
        this.workflowIDs = workflowIDs;
        this.statuses = statuses;
    }

    public UIConfiguration create() throws Exception {

        URL hueURL;
        List<WorkflowGroup> groups;

        if (configFile != null) {
            JsonNode mainNode = objectMapper.readValue(new FileInputStream(configFile), JsonNode.class);

            hueURL = getHueUrl(mainNode);
            groups = getWorkflowGroups(mainNode);
        } else {
            hueURL = defaultHueURL;
            groups = getDefaultGroups(workflowIDs);
        }

        return new UIConfiguration(start, end, tileTimes, groups, statuses, hueURL);
    }

    private URL getHueUrl(JsonNode mainNode) throws MalformedURLException {
        if (defaultHueURL == null) {
            return mainNode.has(HUE_URL_TAG) ? new URL(mainNode.get(HUE_URL_TAG).textValue()) : null;
        }
        return defaultHueURL;
    }

    private List<WorkflowGroup> getWorkflowGroups(JsonNode mainNode) throws com.fasterxml.jackson.core.JsonProcessingException {
        List<WorkflowGroup> configWorkflowGroups = new ArrayList();
        Set<WorkflowID> listedWfs = new TreeSet<>();

        for(JsonNode workflowGroupNode: mainNode.get(WORKFLOW_TO_SLOT_MAP_TAG)) {
            String[] workflowNames = objectMapper.treeToValue(workflowGroupNode.get(WORKFLOWS_TAG), String[].class);

            List<WorkflowID> ids = new ArrayList<>();
            for (String wfName : workflowNames) {
                ids.add(new WorkflowID(wfName));
            }

            String name = workflowGroupNode.get(NAME_TAG).textValue();
            configWorkflowGroups.add(new WorkflowGroup(name, ids));
            listedWfs.addAll(ids);
        }

        listedWfs.removeAll(workflowIDs);
        configWorkflowGroups.add(new WorkflowGroup(UNLISTED_WORKFLOWS_CAPTION, new ArrayList<>(listedWfs)));

        return configWorkflowGroups;
    }

    private List<WorkflowGroup> getDefaultGroups(Set<WorkflowID> workflows) {
        return ImmutableList.of(new WorkflowGroup(DEFAULT_CAPTION, new LinkedList<WorkflowID>(new TreeSet<WorkflowID>(workflows))));
    }
}
