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

import com.collective.celos.database.JDBCStateDatabase;
import com.collective.celos.database.StateDatabaseConnection;
import junit.framework.Assert;
import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCStateDatabaseTest extends AbstractStateDatabaseTest {

    public static final String CLEAR_SLOT_STATE_TABLE = "DELETE FROM SLOTSTATE";

    public static final String CLEAR_RERUN_SLOT_TABLE = "DELETE FROM RERUNSLOT";

    public static final String CLEAR_WORKFLOW_INFO_TABLE = "DELETE FROM WORKFLOWINFO";

    public static final String CLEAR_REGISTERS_TABLE = "DELETE FROM REGISTER";


    public static final String CREATE_SLOT_STATE_TABLE = "CREATE TABLE SLOTSTATE (" +
            "WORKFLOWID VARCHAR(512) NOT NULL, DATE TIMESTAMP NOT NULL, STATUS VARCHAR(512) NOT NULL, " +
            "EXTERNALID VARCHAR(512), RETRYCOUNT INTEGER NOT NULL DEFAULT 0)";

    public static final String CREATE_RERUN_SLOT_TABLE = "CREATE TABLE RERUNSLOT (" +
            "WORKFLOWID VARCHAR(512) NOT NULL, DATE TIMESTAMP NOT NULL, WALLCLOCK TIMESTAMP NOT NULL)";

    public static final String CREATE_WORKFLOW_INFO_TABLE = "CREATE TABLE WORKFLOWINFO (" +
            "WORKFLOWID VARCHAR(512) NOT NULL, PAUSED BOOLEAN NOT NULL)";

    public static final String CREATE_REGISTERS_TABLE = "CREATE TABLE REGISTER (" +
            "BUCKETID VARCHAR(512) NOT NULL, KEY VARCHAR(512) NOT NULL, JSON VARCHAR(512) NOT NULL)";

    private static Server SERVER;
    private static String URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static String USERNAME = "sa";
    private static String PASSWORD = "";
    private static StateDatabaseConnection db;

    @BeforeClass
    public static void setupClass() throws Exception {
        Class.forName("org.h2.Driver");
        SERVER = Server.createTcpServer().start();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(CREATE_SLOT_STATE_TABLE);
                statement.execute(CREATE_RERUN_SLOT_TABLE);
                statement.execute(CREATE_WORKFLOW_INFO_TABLE);
                statement.execute(CREATE_REGISTERS_TABLE);
            }
        }
        JDBCStateDatabase.Config config = new JDBCStateDatabase.Config(URL, USERNAME, PASSWORD);
        db = new JDBCStateDatabase(config).openConnection();
    }

    @AfterClass
    public static void tearDownClass() {
        SERVER.stop();
    }

    @Override
    public StateDatabaseConnection getStateDatabase() {
        return db;
    }

    @Before
    public void setupTest() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(CLEAR_REGISTERS_TABLE);
                statement.execute(CLEAR_RERUN_SLOT_TABLE);
                statement.execute(CLEAR_SLOT_STATE_TABLE);
                statement.execute(CLEAR_WORKFLOW_INFO_TABLE);
            }
        }
    }

    @Test
    public void getAndPutWorksWithExternalID() throws Exception {
        StateDatabaseConnection db = getStateDatabase();
        SlotID slotID = new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-27T14:50Z"));
        Assert.assertEquals(null, db.getSlotState(slotID));
        String externalID = "externalId1";
        String externalID2 = "externalId2";

        SlotState state1 = new SlotState(slotID, SlotState.Status.READY, externalID, 5);
        SlotState state2 = new SlotState(slotID, SlotState.Status.READY, externalID2, 3);

        db.putSlotState(state1);
        SlotState newSlotState = db.getSlotState(slotID);
        Assert.assertEquals(state1, newSlotState);
        Assert.assertEquals(newSlotState.getExternalID(), externalID);
        Assert.assertEquals(newSlotState.getRetryCount(), 5);

        db.putSlotState(state2);
        SlotState newSlotState2 = db.getSlotState(slotID);
        Assert.assertEquals(state2, newSlotState2);
        Assert.assertEquals(newSlotState2.getExternalID(), externalID2);
        Assert.assertEquals(newSlotState2.getRetryCount(), 3);
    }

}
