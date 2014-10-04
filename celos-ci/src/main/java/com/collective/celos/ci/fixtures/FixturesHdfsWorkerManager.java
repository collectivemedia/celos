package com.collective.celos.ci.fixtures;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by akonopko on 9/18/14.
 */
public class FixturesHdfsWorkerManager {

    private CelosCiContext context;
    private Map<String, FixtureWorker> fixtureWorkers;

    public FixturesHdfsWorkerManager(CelosCiContext context) {
        this.context = context;
        this.fixtureWorkers = new HashMap<>();
    }

    public void addFixtureWorker(String type, FixtureWorker worker) {
        this.fixtureWorkers.put(type.toUpperCase(), worker);
    }

    public void processLocalDir(File inputDirLocal) throws Exception {
        for (File typeDir : inputDirLocal.listFiles()) {
            FixtureWorker worker = fixtureWorkers.get(typeDir.getName().toUpperCase());
            if (worker == null) {
                throw new RuntimeException("Cant find fixture worker for " + typeDir.getName());
            }
            worker.process(context, typeDir);
        }
    }

}
