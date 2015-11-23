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
package com.collective.celos.sqlclient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main {

    public static void main(String... args) throws Exception {
        SqlClientCommandLineParser serverCommandLineParser = new SqlClientCommandLineParser();
        SqlClientCommandLine commandLine = serverCommandLineParser.parse(args);

        try(Connection connection = DriverManager.getConnection(commandLine.getUrl(), commandLine.getUsername(), commandLine.getPassword())) {
            try(Statement statement = connection.createStatement()) {
                try (BufferedReader br = new BufferedReader(new FileReader(commandLine.getSqlFile()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        statement.execute(line);
                    }
                }
            }
        }
    }

}
