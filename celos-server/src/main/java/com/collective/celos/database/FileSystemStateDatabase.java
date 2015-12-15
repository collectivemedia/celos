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
package com.collective.celos.database;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import com.collective.celos.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.quartz.CronExpression;

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
    private static final String REGISTER_DIR_NAME = "register";
    private static final Logger LOGGER = Logger.getLogger(FileSystemStateDatabase.class);

    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
    private final File stateDir;
    private final File rerunDir;
    private final File pausedDir;
    private final File registerDir;
    private final FileSystemStateDatabaseConnection instance;
    private final File dir;


    /**
     * Creates a new DB that stores data in the given directory, which must exist.
     */
    public FileSystemStateDatabase(File dir) throws IOException {
        Util.validateDirExists(dir);
        this.stateDir = new File(dir, STATE_DIR_NAME);
        this.rerunDir = new File(dir, RERUN_DIR_NAME);
        this.pausedDir = new File(dir, PAUSED_DIR_NAME);
        this.registerDir = new File(dir, REGISTER_DIR_NAME);
        this.dir = dir;
        this.instance = new FileSystemStateDatabaseConnection();
    }

    @Override
    public StateDatabaseConnection openConnection() {
        return instance;
    }

    public File getDir() {
        return dir;
    }

    private class FileSystemStateDatabaseConnection implements StateDatabaseConnection {

        @Override
        public void close() throws Exception {

        }

        @Override
        public Map<SlotID, SlotState> getSlotStates(WorkflowID id, ScheduledTime start, ScheduledTime end) throws Exception {

            Map<SlotID, SlotState> slotStates = Maps.newHashMap();

            ScheduledTime startBeginOfDay = new ScheduledTime(start.getDateTime().withMillisOfDay(0));
            ScheduledTime endBeginOfDay = new ScheduledTime(end.getDateTime().withMillisOfDay(0));

            ScheduledTime currTime = startBeginOfDay;

            while (!currTime.getDateTime().isAfter(endBeginOfDay.getDateTime())) {
                File dayDir = getDayDir(getWorkflowStateDir(id), currTime);
                if (dayDir.exists() && dayDir.isDirectory()) {
                    slotStates.putAll(getSlotStatesFromDir(id, start, end, dayDir));
                }
                currTime = new ScheduledTime(currTime.getDateTime().plusDays(1));
            }

            return slotStates;
        }

        @Override
        public SlotState getSlotState(SlotID id) throws Exception {
            File file = getSlotStateFile(id);
            if (!file.exists()) {
                return null;
            } else {
                return readSlotStateFromFile(id, file);
            }
        }

        @Override
        public void putSlotState(SlotState state) throws Exception {
            File file = getSlotStateFile(state.getSlotID());
            writeJson(state.toJSONNode(), file);
        }

        private Map<SlotID, SlotState> getSlotStatesFromDir(WorkflowID id, ScheduledTime start, ScheduledTime end, File dayDir) throws IOException {
            Map<SlotID, SlotState> slotStates = Maps.newHashMap();
            for (File file : dayDir.listFiles()) {
                ScheduledTime time = new ScheduledTime(dayDir.getName() + "T" + file.getName());
                if (!time.getDateTime().isBefore(start.getDateTime()) && time.getDateTime().isBefore(end.getDateTime())) {
                    SlotID slotID = new SlotID(id, time);
                    slotStates.put(slotID, readSlotStateFromFile(slotID, file));
                }
            }
            return slotStates;
        }

        private SlotState readSlotStateFromFile(SlotID id, File file) throws IOException {
            return SlotState.fromJSONNode(id, readJson(file));
        }

        /**
         * Returns the directory containing state for the slot's workflow.
         */
        private File getWorkflowStateDir(WorkflowID id) {
            return new File(stateDir, id.toString());
        }

        /**
         * Returns the directory containing rerun info for the slot's workflow.
         */
        private File getWorkflowRerunDir(WorkflowID id) {
            return new File(rerunDir, id.toString());
        }

        private File getWorkflowPauseFile(WorkflowID id) {
            return new File(pausedDir, id.toString());
        }

        private File getSlotStateFile(SlotID slotID) {
            return new File(getDayDir(getWorkflowStateDir(slotID.getWorkflowID()), slotID.getScheduledTime()), getFileName(slotID));
        }

        private File getSlotRerunFile(SlotID slotID) {
            return new File(getDayDir(getWorkflowRerunDir(slotID.getWorkflowID()), slotID.getScheduledTime()), getFileName(slotID));
        }

        /**
         * Returns the directory containing a day's data inside the workflow dir.
         */
        private File getDayDir(File superDir, ScheduledTime time) {
            return new File(superDir, formatter.formatDatestamp(time));
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
                        RerunState st = RerunState.fromJSONNode(readJson(rerunFile));
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
            } else if (file.exists()) {
                FileUtils.forceDelete(file);
            }
        }

        //// Registers

        private File getBucketDir(BucketID bucket) {
            return new File(registerDir, bucket.toString());
        }

        private File getRegisterFile(BucketID bucket, RegisterKey key) {
            return new File(getBucketDir(bucket), key.toString());
        }

        @Override
        public JsonNode getRegister(BucketID bucket, RegisterKey key) throws Exception {
            Util.requireNonNull(bucket);
            Util.requireNonNull(key);
            File registerFile = getRegisterFile(bucket, key);
            if (!registerFile.exists()) {
                return null;
            } else {
                return readJson(registerFile);
            }
        }

        @Override
        public Set<RegisterKey> getRegisterKeys(BucketID bucketId, String prefix) throws Exception {
            File bucket = getBucketDir(bucketId);
            Set<RegisterKey> keys = Sets.newHashSet();
            for (File file: bucket.listFiles()) {
                if (StringUtils.isEmpty(prefix) || file.getName().startsWith(prefix)) {
                    keys.add(new RegisterKey(file.getName()));
                }
            }
            return keys;
        }

        @Override
        public void putRegister(BucketID bucket, RegisterKey key, JsonNode value) throws Exception {
            Util.requireNonNull(bucket);
            Util.requireNonNull(key);
            Util.requireNonNull(value);
            writeJson(value, getRegisterFile(bucket, key));
        }

        @Override
        public void deleteRegister(BucketID bucket, RegisterKey key) throws Exception {
            Util.requireNonNull(bucket);
            Util.requireNonNull(key);
            File registerFile = getRegisterFile(bucket, key);
            if (registerFile.exists()) {
                registerFile.delete();
            }
        }

        @Override
        public Iterable<Map.Entry<RegisterKey, JsonNode>> getAllRegisters(BucketID bucket) throws Exception {
            Map<RegisterKey, JsonNode> registers = new HashMap<>();
            File bucketDir = getBucketDir(bucket);
            if (bucketDir.exists()) {
                File[] registerFiles = bucketDir.listFiles();
                for(File f : registerFiles) {
                    registers.put(new RegisterKey(f.getName()), readJson(f));
                }
                return registers.entrySet();
            } else {
                return Collections.emptySet();
            }
        }

        private ObjectNode readJson(File file) throws IOException {
            String json = FileUtils.readFileToString(file, CHARSET);
            return (ObjectNode) Util.JSON_READER.readTree(json);
        }

        private void writeJson(JsonNode obj, File file) throws IOException {
            String json = Util.JSON_WRITER.writeValueAsString(Util.requireNonNull(obj));
            FileUtils.forceMkdir(file.getParentFile());
            FileUtils.write(file, json, CHARSET);
        }
    };
}
