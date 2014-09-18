package com.collective.celos.cd.fixtures;

import com.collective.celos.cd.config.CelosCdContext;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by akonopko on 9/18/14.
 */
public class FixturesHdfsWorkerManager {

    private CelosCdContext context;
    private Map<String, ? extends AbstractFixtureWorker> fixtureWorkers;

    public FixturesHdfsWorkerManager(CelosCdContext context, Map<String, ? extends AbstractFixtureWorker> fixtureWorkers) {
        this.context = context;
        this.fixtureWorkers = fixtureWorkers;
    }


    public void processLocalDir(String inputDirLocalPath) throws Exception {
        final File inputDirLocal = new File(inputDirLocalPath);
        if (StringUtils.isNotEmpty(context.getHdfsPrefix()) && inputDirLocal.exists()) {
            for (File typeDir : inputDirLocal.listFiles()) {
                AbstractFixtureWorker worker = fixtureWorkers.get(typeDir.getName().toUpperCase());
                if (worker == null) {
                    throw new RuntimeException("Cant find fixture worker for " + typeDir.getName());
                }
                worker.process(typeDir);
            }
        }
    }

}
