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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.collective.celos.database.StateDatabase;
import com.collective.celos.database.StateDatabaseConnection;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;

/**
 * Simple implementation of StateDatabase that stores everything in a Map.
 */
public class MemoryStateDatabase implements StateDatabase {

    protected final Map<SlotID, SlotState> map = new HashMap<>();
    protected final Map<SlotID, ScheduledTime> rerun = new ConcurrentHashMap<>();
    protected final Set<WorkflowID> pausedWorkflows = new HashSet<>();
    // Use SortedMap for easier testing of register contents
    protected final SortedMap<BucketID, SortedMap<RegisterKey, JsonNode>> registers = new TreeMap<>();

    private final MemoryStateDatabaseConnection instance = new MemoryStateDatabaseConnection();

    @Override
    public StateDatabaseConnection openConnection() {
        return getMemoryStateDatabaseConnection();
    }

    public MemoryStateDatabaseConnection getMemoryStateDatabaseConnection() {
        return instance;
    }

    protected class MemoryStateDatabaseConnection implements StateDatabaseConnection {
        @Override
        public Map<SlotID, SlotState> getSlotStates(WorkflowID id, ScheduledTime start, ScheduledTime end) throws Exception {
            Map<SlotID, SlotState> slotStates = Maps.newHashMap();
            for (Map.Entry<SlotID, SlotState> entry : map.entrySet()) {
                DateTime dateTime = entry.getKey().getScheduledTime().getDateTime();
                if (!dateTime.isBefore(start.getDateTime()) && dateTime.isBefore(end.getDateTime())) {
                    slotStates.put(entry.getKey(), entry.getValue());
                }
            }
            return slotStates;
        }

        @Override
        public SlotState getSlotState(SlotID id) throws Exception {
            return map.get(id);
        }

        @Override
        public void putSlotState(SlotState state) throws Exception {
            map.put(state.getSlotID(), state);
        }

        public int size() {
            return map.size();
        }

        @Override
        public void markSlotForRerun(SlotID slot, ScheduledTime now) throws Exception {
            // Doesn't implement GC for rerun
            rerun.put(slot, now);
        }

        @Override
        public boolean isPaused(WorkflowID workflowID) {
            return pausedWorkflows.contains(workflowID);
        }

        @Override
        public SortedSet<ScheduledTime> getTimesMarkedForRerun(WorkflowID workflowID, ScheduledTime now) throws Exception {
            SortedSet<ScheduledTime> res = new TreeSet<>();
            for (Map.Entry<SlotID, ScheduledTime> entry : rerun.entrySet()) {
                if (entry.getKey().getWorkflowID().equals(workflowID)) {
                    res.add(entry.getKey().getScheduledTime());
                    RerunState rerunState = new RerunState(entry.getValue());
                    if (rerunState.isExpired(now)) {
                        rerun.remove(entry.getKey());
                    }
                }
            }
            return res;
        }

        @Override
        public void setPaused(WorkflowID workflowID, boolean paused) {
            if (paused) {
                pausedWorkflows.add(workflowID);
            } else {
                pausedWorkflows.remove(workflowID);
            }
        }
        
        @Override
        public JsonNode getRegister(BucketID bucket, RegisterKey key) throws Exception {
            Util.requireNonNull(bucket);
            Util.requireNonNull(key);
            Map<RegisterKey, JsonNode> bucketMap = registers.get(bucket);
            if (bucketMap == null) {
                return null;
            } else {
                return bucketMap.get(key);
            }
        }

        @Override
        public Set<RegisterKey> getRegisterKeys(BucketID bucket, String prefix) throws Exception {
            Set<RegisterKey> keys = registers.get(bucket).keySet();
            if (StringUtils.isEmpty(prefix)) {
                return keys;
            } else {
                return keys.stream().filter( x -> x.toString().startsWith(prefix)).collect(Collectors.toSet());
            }
        }

        @Override
        public void putRegister(BucketID bucket, RegisterKey key, JsonNode value) throws Exception {
            Util.requireNonNull(bucket);
            Util.requireNonNull(key);
            Util.requireNonNull(value);
            SortedMap<RegisterKey, JsonNode> bucketMap = registers.get(bucket);
            if (bucketMap == null) {
                bucketMap = new TreeMap<RegisterKey, JsonNode>();
                registers.put(bucket, bucketMap);
            }
            bucketMap.put(key, value);
        }
        
        @Override
        public void deleteRegister(BucketID bucket, RegisterKey key) throws Exception {
            Util.requireNonNull(bucket);
            Util.requireNonNull(key);
            SortedMap<RegisterKey, JsonNode> bucketMap = registers.get(bucket);
            if (bucketMap == null) {
                return;
            } else {
                bucketMap.remove(key);
            }
        }

        @Override
        public void deleteRegister(BucketID bucket, String prefix) throws Exception {
            Util.requireNonNull(bucket);
            Util.requireNonNull(prefix);
            SortedMap<RegisterKey, JsonNode> bucketMap = registers.get(bucket);
            if (bucketMap == null) {
                return;
            } else {
                for (RegisterKey key : new ArrayList<>(bucketMap.keySet())) {
                    if (key.toString().startsWith(prefix)) {
                        bucketMap.remove(key);
                    }
                }
            }
        }

        @Override
        public Iterable<Map.Entry<RegisterKey, JsonNode>> getAllRegisters(BucketID bucket) throws Exception {
            SortedMap<RegisterKey, JsonNode> bucketMap = registers.get(bucket);
            if (bucketMap == null) {
                return Collections.emptySet();
            } else {
                return bucketMap.entrySet();
            }
        }

        @Override
        public void close() throws Exception {
            
        }
    };

}
