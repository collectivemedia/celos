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
    private Map<String, AbstractFixtureFileWorker> fixtureWorkers;

    public FixturesHdfsWorkerManager(CelosCiContext context, Map<String, ? extends AbstractFixtureFileWorker> fixtureWorkers) {
        this.context = context;
        this.fixtureWorkers = new HashMap<>();
        for (Map.Entry<String, ? extends AbstractFixtureFileWorker> entry : fixtureWorkers.entrySet()) {
            this.fixtureWorkers.put(entry.getKey().toUpperCase(), entry.getValue());
        }
    }


    public void processLocalDir(String inputDirLocalPath) throws Exception {
        final File inputDirLocal = new File(inputDirLocalPath);
        if (StringUtils.isNotEmpty(context.getHdfsPrefix()) && inputDirLocal.exists()) {
            for (File typeDir : inputDirLocal.listFiles()) {
                AbstractFixtureFileWorker worker = fixtureWorkers.get(typeDir.getName().toUpperCase());
                if (worker == null) {
                    throw new RuntimeException("Cant find fixture worker for " + typeDir.getName());
                }
                worker.process(context, typeDir);
            }
        }
    }

}
