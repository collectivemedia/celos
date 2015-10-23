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
package com.collective.celos;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Brutally simple persistent implementation of StateDatabase
 * that stores slot states as JSON files in the file system.
 * 
 * The database has a top-level directory, with one sub directory, called state.
 * 
 * Inside the state directory, there is a subdirectory for each workflow. 
 * Inside each workflow directory is one directory per day.  Inside each 
 * day directory is one JSON file per workflow run.
 * 
 * state/
 *   workflow-1/
 *     2013-12-02/
 *       16:00:00.000Z
 *       17:00:00.000Z
 *     ...
 *   workflow-2/
 *     2013-12-02/
 *       16:00:00.000Z
 *       17:00:00.000Z
 * rerun/
 *   workflow-1/
 *     2013-12-02/
 *       16:00:00.000Z
 *     ...
 *   workflow-2/
 *     2013-12-02/
 *       17:00:00.000Z
 *     ...
 *   ...
 * paused/
 *   workflow-1
 *   workflow-2
 *   ...
 *
 * A JSON state file looks like this:
 * 
 * {"status":"RUNNING","externalID":"23873218-13202130978213-W"}
 * 
 * A JSON rerun file looks like this:
 * 
 * {"rerunTime":"2015-09-06T20:21Z"}
 */
public class FileSystemStateDatabase implements StateDatabase {

    private static final String CHARSET = "UTF-8";
    private static final String STATE_DIR_NAME = "state";
    private static final String RERUN_DIR_NAME = "rerun";
    private static final String PAUSED_DIR_NAME = "paused";
    private static final Logger LOGGER = Logger.getLogger(FileSystemStateDatabase.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
    private final File stateDir;
    private final File rerunDir;
    private final File pausedDir;
    
    /**
     * Creates a new DB that stores data in the given directory, which must exist.
     */
    public FileSystemStateDatabase(File dir) throws IOException {
        if (!Util.requireNonNull(dir).exists()) {
            throw new IOException("Database directory " + dir + " doesn't exist.");
        }
        stateDir = new File(dir, STATE_DIR_NAME);
        rerunDir = new File(dir, RERUN_DIR_NAME);
        pausedDir = new File(dir, PAUSED_DIR_NAME);
    }

    private final Map<SlotID, SlotState> cache = Maps.newHashMapWithExpectedSize(24 * 7 * 10000);
    private final Set<SlotID> cacheWaits = Sets.newHashSetWithExpectedSize(24 * 7 * 10000);

    @Override
    public SlotState getSlotState(SlotID id) throws Exception {
        SlotState slotState = cache.get(id);
        if (slotState != null) {
            return slotState;
        }
        if (cacheWaits.contains(id)) {
            return null;
        } else if (!getSlotStateFile(id).exists()) {
            cacheWaits.add(id);
            return null;
        }

        String json = FileUtils.readFileToString(getSlotStateFile(id), CHARSET);
        slotState = SlotState.fromJSONNode(id, mapper.readTree(json));
        cache.put(id, slotState);
        return slotState;
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        cache.put(state.getSlotID(), state);
        cacheWaits.remove(state.getSlotID());
        File file = getSlotStateFile(state.getSlotID());
        writeJson(state.toJSONNode(), file);
    }
    
    /** Returns the directory containing state for the slot's workflow. */
    private File getWorkflowStateDir(WorkflowID id) {
        return new File(stateDir, id.toString());
    }
    
    /** Returns the directory containing rerun info for the slot's workflow. */
    private File getWorkflowRerunDir(WorkflowID id) {
        return new File(rerunDir, id.toString());
    }

    private File getWorkflowPauseFile(WorkflowID id) {
        return new File(pausedDir, id.toString());
    }

    private File getSlotStateFile(SlotID slotID) {
        return new File(getDayDir(getWorkflowStateDir(slotID.getWorkflowID()), slotID), getFileName(slotID));
    }
    
    private File getSlotRerunFile(SlotID slotID) {
        return new File(getDayDir(getWorkflowRerunDir(slotID.getWorkflowID()), slotID), getFileName(slotID));
    }

    /** Returns the directory containing a day's data inside the workflow dir. */
    private File getDayDir(File superDir, SlotID slotID) {
        return new File(superDir, formatter.formatDatestamp(slotID.getScheduledTime()));
    }

    private String getFileName(SlotID slotID) {
        return formatter.formatTimestamp(slotID.getScheduledTime());
    }

    @Override
    public void markSlotForRerun(SlotID slotID, ScheduledTime now) throws Exception {
        RerunState st = new RerunState(now);
        File file = getSlotRerunFile(slotID);
        writeJson(st.toJSONNode(), file);
    }

    @Override
    public SortedSet<ScheduledTime> getTimesMarkedForRerun(WorkflowID workflowID, ScheduledTime now) throws Exception {
        SortedSet<ScheduledTime> res = new TreeSet<>();
        File wfDir = getWorkflowRerunDir(workflowID);
        if (wfDir.exists()) {
            for (File dayDir : wfDir.listFiles()) {
                for (File rerunFile : dayDir.listFiles()) {
                    String json = FileUtils.readFileToString(rerunFile, CHARSET);
                    RerunState st = RerunState.fromJSONNode((ObjectNode) mapper.readTree(json));
                    ScheduledTime t = new ScheduledTime(dayDir.getName() + "T" + rerunFile.getName());
                    res.add(t);
                    if (st.isExpired(now)) {
                        LOGGER.info("Expiring rerun file: " + rerunFile);
                        rerunFile.delete();
                    }
                }
            }
        }
        return res;
    }

    @Override
    public boolean isPaused(WorkflowID workflowID) {
        return getWorkflowPauseFile(workflowID).exists();
    }

    @Override
    public void setPaused(WorkflowID workflowID, boolean paused) throws IOException {
        File file = getWorkflowPauseFile(workflowID);
        if (paused) {
            FileUtils.touch(file);
        } else {
            FileUtils.forceDelete(file);
        }
    }

    private void writeJson(JsonNode obj, File file) throws IOException {
        String json = mapper.writeValueAsString(obj);
        FileUtils.forceMkdir(file.getParentFile());
        FileUtils.write(file, json, CHARSET);
    }

}
