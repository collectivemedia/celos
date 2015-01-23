package com.collective.celos.ci;

import com.collective.celos.Util;
import com.collective.celos.ci.config.CelosCiCommandLine;
import com.collective.celos.ci.config.ContextParser;
import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.mode.DeployTask;
import com.collective.celos.ci.mode.TestTask;
import com.collective.celos.ci.mode.UndeployTask;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.avro.Schema;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class CelosCi {

    private final static String READ_TABLE_DATA = "SELECT * FROM %s.%s LIMIT 1";

    private static final String DESCRIBE_TABLE_PATTERN = "DESCRIBE %s.%s";

    public static void main(String... args) throws Exception {

        //left here for convenience while debugging
        args = "--testDir /home/akonopko/work/Pythia/harmony/src/test/celos-ci --deployDir /home/akonopko/work/Pythia/harmony/build/celos_deploy --target sftp://celos001/home/akonopko/testing.json --workflowName grand_central --mode TEST".split(" ");

        ContextParser contextParser = new ContextParser();
        CelosCiCommandLine commandLine = contextParser.parse(args);

        CelosCi celosCi = createCelosCi(commandLine);
        celosCi.start();
    }

    private static List<String> getColumnDefinitionLines(ResultSet res) throws SQLException {
        List<String> tableColumns = Lists.newArrayList();
        while (res.next()) {
            String column = res.getString(1);
            String type = res.getString(2);
            if (column != null && type != null && !column.startsWith("#")) {
                tableColumns.add(column.trim() + " " + type.trim());
            }
        }
        return tableColumns;
    }


    public static CelosCi createCelosCi(CelosCiCommandLine commandLine) throws Exception {

        if (commandLine.getMode() == CelosCiContext.Mode.TEST) {
            return new TestTask(commandLine);
        } else if (commandLine.getMode() == CelosCiContext.Mode.DEPLOY) {
            return new DeployTask(commandLine);
        } else if (commandLine.getMode() == CelosCiContext.Mode.UNDEPLOY) {
            return new UndeployTask(commandLine);
        }
        throw new IllegalStateException("Unknown mode " + commandLine.getMode());
    }

    public abstract void start() throws Exception;

}
