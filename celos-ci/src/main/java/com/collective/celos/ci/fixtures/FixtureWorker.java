package com.collective.celos.ci.fixtures;

import com.collective.celos.ci.config.deploy.CelosCiContext;

import java.io.File;

/**
 * Created by akonopko on 10/4/14.
 */
public interface FixtureWorker {

    public void process(CelosCiContext context, File localDir) throws Exception;
}
