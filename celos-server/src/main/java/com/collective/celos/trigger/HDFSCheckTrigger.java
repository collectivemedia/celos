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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.ScheduledTimeFormatter;
import com.collective.celos.Scheduler;
import com.collective.celos.Util;

/**
 * Check in HDFS for a data dependency.
 */
public class HDFSCheckTrigger extends Trigger {

    private static final Map<String, FileSystem> cachedFSs = new HashMap<>();
    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();

    private static final Logger LOGGER = Logger.getLogger(HDFSCheckTrigger.class);
    private final FileSystem fs;
    private final String rawPathString;
    private final String fsString;

    public HDFSCheckTrigger(String rawPathString, String fsString) throws Exception {
        this.rawPathString = Util.requireNonNull(rawPathString);
        this.fsString = Util.requireNonNull(fsString);

        if (cachedFSs.containsKey(fsString)) {
            this.fs = cachedFSs.get(fsString);
        } else {
            Configuration conf = new Configuration();
            //required due http://stackoverflow.com/questions/17265002/hadoop-no-filesystem-for-scheme-file
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

            addFileToConfiguration(conf, "hdfs-site.xml");
            addFileToConfiguration(conf, "core-site.xml");

            this.fs = FileSystem.get(new URI(fsString), conf);
            cachedFSs.put(fsString, this.fs);
        }
    }

    private void addFileToConfiguration(Configuration conf, String fileName) throws FileNotFoundException {
        InputStream fileStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (fileStream != null) {
            conf.addResource(fileStream);
        }
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        Path path = new Path(formatter.replaceTimeTokens(getRawPathString(), scheduledTime));
        LOGGER.info("Checking HDFS path: " + path);
        boolean ready = getFs().exists(path);
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

    public FileSystem getFs() {
        return fs;
    }
}
