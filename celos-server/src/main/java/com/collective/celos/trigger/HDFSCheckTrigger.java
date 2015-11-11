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
package com.collective.celos.trigger;

import com.collective.celos.*;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

/**
 * Check in HDFS for a data dependency.
 */
public class HDFSCheckTrigger extends Trigger {

    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();

    private static final Logger LOGGER = Logger.getLogger(HDFSCheckTrigger.class);
    private final String rawPathString;
    private final String fsString;

    public HDFSCheckTrigger(String rawPathString, String fsString) throws Exception {
        this.rawPathString = Util.requireNonNull(rawPathString);
        this.fsString = Util.requireNonNull(fsString);
    }

    @Override
    public TriggerStatus getTriggerStatus(StateDatabaseConnection connection, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        Path path = new Path(formatter.replaceTimeTokens(getRawPathString(), scheduledTime));
//        LOGGER.info("Checking HDFS path: " + path);
        boolean ready = FSThreadLocal.getOrCreate(fsString).exists(path);
        return makeTriggerStatus(ready, humanReadableDescription(ready, path));
    }

    private String humanReadableDescription(boolean ready, Path path) {
        if (ready) {
            return "HDFS path " + path.toString() + " is ready";
        } else {
            return "HDFS path " + path.toString() + " is not ready";
        }
    }

    public String getRawPathString() {
        return rawPathString;
    }

    public String getFsString() {
        return fsString;
    }

}
