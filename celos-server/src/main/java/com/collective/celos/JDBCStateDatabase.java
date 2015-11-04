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

import com.collective.celos.servlet.AbstractServlet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class JDBCStateDatabase implements StateDatabase {

    private static final String SELECT_SINGLE_SLOT = "SELECT STATUS, EXTERNALID, RETRYCOUNT FROM SLOTSTATE WHERE WORKFLOWID = ? AND DATE = ?";
    private static final String INSERT_SLOT_STATE = "INSERT INTO SLOTSTATE(WORKFLOWID, DATE, STATUS, EXTERNALID, RETRYCOUNT) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SLOT_STATE = "UPDATE SLOTSTATE SET STATUS=?, EXTERNALID=?, RETRYCOUNT=? WHERE WORKFLOWID=? AND DATE=?";
    private static final String SELECT_SLOTS_BY_PERIOD = "SELECT STATUS, EXTERNALID, RETRYCOUNT, DATE FROM SLOTSTATE WHERE WORKFLOWID = ? AND DATE >= ? AND DATE < ?";
    private static final String INSERT_RERUN_SLOT = "INSERT INTO RERUNSLOT(WORKFLOWID, DATE) VALUES (?, ?)";
    private static final String SELECT_RERUN_SLOTS = "SELECT DATE FROM RERUNSLOT WHERE WORKFLOWID = ?";
    private static final String DELETE_RERUN_SLOTS = "DELETE FROM RERUNSLOT WHERE WORKFLOWID = ? AND DATE = ?";
    private static final String INSERT_PAUSE_WORKFLOW = "INSERT INTO WORKFLOWINFO(WORKFLOWID, PAUSED) VALUES (?, ?)";
    private static final String UPDATE_PAUSE_WORKFLOW = "UPDATE WORKFLOWINFO SET PAUSED = ? WHERE WORKFLOWID = ?";
    private static final String SELECT_PAUSE_WORKFLOW = "SELECT PAUSED FROM WORKFLOWINFO WHERE WORKFLOWID = ?";

    private static final String STATUS_PARAM = "STATUS";
    private static final String EXTERNAL_ID_PARAM = "EXTERNALID";
    private static final String RETRY_COUNT_PARAM = "RETRYCOUNT";
    private static final String DATE_PARAM = "DATE";
    private static final String PAUSED_PARAM = "PAUSED";

    private static final Logger LOGGER = Logger.getLogger(AbstractServlet.class);


    private final String url;
    private final String name;
    private final String password;

    public JDBCStateDatabase(String url, String name, String password) {
        this.url = url;
        this.name = name;
        this.password = password;
    }

    @Override
    public StateDatabaseConnection openConnection() throws Exception {
        return new JDBCStateDatabaseConnectionConnection(url, name, password);
    }

    private class JDBCStateDatabaseConnectionConnection implements StateDatabaseConnection {

        private final Connection connection;

        public JDBCStateDatabaseConnectionConnection(String url, String name, String password) throws SQLException {
            this.connection = DriverManager.getConnection(url, name, password);
        }

        @Override
        public void close() {
            try {
                connection.close();
            } catch (Exception e) {
                LOGGER.error("Failed to close DB connection", e);
            }
        }

        @Override
        public Map<SlotID, SlotState> getSlotStates(WorkflowID id, ScheduledTime start, ScheduledTime end) throws Exception {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SLOTS_BY_PERIOD)) {
                preparedStatement.setString(1, id.toString());
                preparedStatement.setTimestamp(2, Util.toTimestamp(start));
                preparedStatement.setTimestamp(3, Util.toTimestamp(end));
                Map<SlotID, SlotState> slotStates = Maps.newHashMap();
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        SlotState.Status status = SlotState.Status.valueOf(resultSet.getString(STATUS_PARAM));
                        String externalId = resultSet.getString(EXTERNAL_ID_PARAM);
                        int retryCount = resultSet.getInt(RETRY_COUNT_PARAM);
                        ScheduledTime date = Util.fromTimestamp(resultSet.getTimestamp(DATE_PARAM));
                        SlotID slotID = new SlotID(id, date);
                        slotStates.put(slotID, new SlotState(slotID, status, externalId, retryCount));
                    }
                }
                return slotStates;
            }
        }

        @Override
        public SlotState getSlotState(SlotID slotId) throws Exception {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_SINGLE_SLOT)) {
                preparedStatement.setString(1, slotId.getWorkflowID().toString());
                preparedStatement.setTimestamp(2, Util.toTimestamp(slotId.getScheduledTime()));
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }
                    SlotState.Status status = SlotState.Status.valueOf(resultSet.getString(STATUS_PARAM));
                    String externalId = resultSet.getString(EXTERNAL_ID_PARAM);
                    int retryCount = resultSet.getInt(RETRY_COUNT_PARAM);
                    return new SlotState(slotId, status, externalId, retryCount);
                }
            }
        }

        @Override
        public void putSlotState(SlotState state) throws Exception {
            SlotState slotState = getSlotState(state.slotID);
            if (slotState == null) {
                try (PreparedStatement statement = connection.prepareStatement(INSERT_SLOT_STATE)) {
                    statement.setString(1, state.getSlotID().getWorkflowID().toString());
                    statement.setTimestamp(2, Util.toTimestamp(state.getSlotID().getScheduledTime()));
                    statement.setString(3, state.getStatus().toString());
                    statement.setString(4, state.getExternalID());
                    statement.setInt(5, state.getRetryCount());
                    statement.execute();
                }
            } else {
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_SLOT_STATE)) {
                    statement.setString(1, state.getStatus().toString());
                    statement.setString(2, state.getExternalID());
                    statement.setInt(3, state.getRetryCount());
                    statement.setString(4, state.getSlotID().getWorkflowID().toString());
                    statement.setTimestamp(5, Util.toTimestamp(state.getSlotID().getScheduledTime()));
                    statement.execute();
                }
            }

        }

        @Override
        public void markSlotForRerun(SlotID slot, ScheduledTime now) throws Exception {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_RERUN_SLOT)) {
                statement.setString(1, slot.getWorkflowID().toString());
                statement.setTimestamp(2, Util.toTimestamp(slot.getScheduledTime()));
                statement.execute();
            }
        }

        @Override
        public SortedSet<ScheduledTime> getTimesMarkedForRerun(WorkflowID workflowID, ScheduledTime now) throws Exception {
            TreeSet<ScheduledTime> rerunTimes = new TreeSet<>();
            List<RerunState> rerunStates = Lists.newArrayList();
            try (PreparedStatement statement = connection.prepareStatement(SELECT_RERUN_SLOTS)) {
                statement.setString(1, workflowID.toString());
                try(ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        ScheduledTime time = Util.fromTimestamp(resultSet.getTimestamp(DATE_PARAM));
                        RerunState rerunState = new RerunState(time);
                        rerunTimes.add(time);
                        if (rerunState.isExpired(now)) {
                            rerunStates.add(rerunState);
                        }
                    }
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(DELETE_RERUN_SLOTS)) {
                for (RerunState rerunState : rerunStates) {
                    statement.setString(1, workflowID.toString());
                    statement.setTimestamp(2, Util.toTimestamp(rerunState.getRerunTime()));
                    statement.execute();
                }
            }
            return rerunTimes;
        }

        @Override
        public boolean isPaused(WorkflowID workflowID) throws Exception {
            return Boolean.TRUE.equals(getPausedWrapped(workflowID));
        }

        private Boolean getPausedWrapped(WorkflowID workflowID) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_PAUSE_WORKFLOW)) {
                statement.setString(1, workflowID.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean(PAUSED_PARAM);
                    } else {
                        return null;
                    }
                }
            }
        }

        @Override
        public void setPaused(WorkflowID workflowID, boolean paused) throws Exception {
            if (getPausedWrapped(workflowID) == null) {
                if (paused) {
                    try (PreparedStatement statement = connection.prepareStatement(INSERT_PAUSE_WORKFLOW)) {
                        statement.setString(1, workflowID.toString());
                        statement.setBoolean(2, paused);
                        statement.execute();
                    }
                }
            } else {
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_PAUSE_WORKFLOW)) {
                    statement.setBoolean(1, paused);
                    statement.setString(2, workflowID.toString());
                    statement.execute();
                }
            }
        }
    }

}
