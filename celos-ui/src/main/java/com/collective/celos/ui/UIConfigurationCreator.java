package com.collective.celos.ui;

import com.collective.celos.ScheduledTime;
import com.collective.celos.WorkflowID;
import com.collective.celos.WorkflowStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;

public class UIConfigurationCreator {

    public static final String WORKFLOW_TO_SLOT_MAP_TAG = "groups";
    public static final String WORKFLOWS_TAG = "workflows";
    public static final String NAME_TAG = "name";
    public static final String UNLISTED_WORKFLOWS_CAPTION = "Unlisted workflows";
    public static final String DEFAULT_CAPTION = "All Workflows";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UIConfiguration create(URL hueURL, File configFile, ScheduledTime end, NavigableSet<ScheduledTime> tileTimes, ScheduledTime start, Set<WorkflowID> workflowIDs, Map<WorkflowID, WorkflowStatus> statuses) throws Exception {

        List<WorkflowGroup> groups;

        if (configFile != null) {
            JsonNode mainNode = objectMapper.readValue(new FileInputStream(configFile), JsonNode.class);

            groups = getWorkflowGroups(mainNode, workflowIDs);
        } else {
            groups = getDefaultGroups(workflowIDs);
        }

        return new UIConfiguration(start, end, tileTimes, groups, statuses, hueURL);
    }

    private List<WorkflowGroup> getWorkflowGroups(JsonNode mainNode, Set<WorkflowID> workflowIDs) throws com.fasterxml.jackson.core.JsonProcessingException {
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
