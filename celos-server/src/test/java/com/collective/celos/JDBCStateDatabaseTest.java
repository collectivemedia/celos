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

import org.h2.tools.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class JDBCStateDatabaseTest extends AbstractStateDatabaseTest {

    public static final String CREATE_SLOT_STATE_TABLE = "CREATE TABLE SLOTSTATE (" +
            "WORKFLOWID VARCHAR(25) NOT NULL, DATE TIMESTAMP NOT NULL, STATUS VARCHAR(20) NOT NULL, " +
            "EXTERNALID VARCHAR(25), RETRYCOUNT INTEGER NOT NULL DEFAULT 0)";

    public static final String CREATE_RERUN_SLOT_TABLE = "CREATE TABLE RERUNSLOT (" +
            "WORKFLOWID VARCHAR(25) NOT NULL, DATE TIMESTAMP NOT NULL)";

    private static Server SERVER;
    private static String URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static String USERNAME = "sa";
    private static String PASSWORD = "";
    private static StateDatabase db;

    @BeforeClass
    public static void setupClass() throws Exception {
        Class.forName("org.h2.Driver");
        SERVER = Server.createTcpServer().start();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(CREATE_SLOT_STATE_TABLE);
                statement.execute(CREATE_RERUN_SLOT_TABLE);
            }
        }
        db = new JDBCStateDatabase(URL, USERNAME, PASSWORD);
    }

    @AfterClass
    public static void tearDownClass() {
        SERVER.stop();
    }

    @Override
    public StateDatabase getStateDatabase() {
        return db;
    }

}
