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
package com.collective.celos.ci;

import com.collective.celos.ci.config.CiCommandLine;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

public abstract class CelosCi3 {

    public static void main(String... args) throws Throwable {

//        left here for convenience while debugging
//        args = "--testDir /home/akonopko/work/Pythia/harmony/src/test/celos-ci --deployDir /home/akonopko/work/Pythia/harmony/build/celos_deploy --target sftp://celos001/home/akonopko/testing.json --workflowName grand_central --mode TEST".split(" ");

//        CiCommandLineParser contextParser = new CiCommandLineParser();
//        CiCommandLine commandLine = contextParser.parse(args);
//
//        CelosCi celosCi = createCelosCi(commandLine);
//        celosCi.start();


        Connection connection = null;
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        String url = "jdbc:postgresql://127.0.0.1:5432/akonopko";
        //Имя пользователя БД
        String name = "akonopko";
        //Пароль
        String password = "akonopko";
        try {
            for (int i = 0; i < 50; i ++)
            tryIt();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

    }

    private static void tryIt() throws IOException {
        long l1 = System.currentTimeMillis();

        for (int i=0; i < 500; i++) {
            FileOutputStream stream = new FileOutputStream("/home" + "/akonopko/" + "work/celos/123");
            IOUtils.write("some data", stream);
            stream.close();
        }


        long l2 = System.currentTimeMillis();

        System.out.println(l2-l1);
    }

    public static CelosCi3 createCelosCi(CiCommandLine commandLine) throws Exception {

//        if (commandLine.getMode() == CelosCiContext.Mode.TEST) {
//            return new TestTask(commandLine);
//        } else if (commandLine.getMode() == CelosCiContext.Mode.DEPLOY) {
//            return new DeployTask(commandLine);
//        } else if (commandLine.getMode() == CelosCiContext.Mode.UNDEPLOY) {
//            return new UndeployTask(commandLine);
//        }
        throw new IllegalStateException("Unknown mode " + commandLine.getMode());
    }

    public abstract void start() throws Throwable;

}
