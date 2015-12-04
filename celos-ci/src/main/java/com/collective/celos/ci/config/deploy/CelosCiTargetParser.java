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
package com.collective.celos.ci.config.deploy;

import com.collective.celos.Util;
import com.collective.celos.ci.deploy.JScpWorker;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

public class CelosCiTargetParser {

    /**
     * Mandatory target properties
     */
    public static final String WORKFLOWS_DIR_URI = "workflows.dir.uri";
    public static final String HADOOP_HDFS_SITE_XML = "hadoop.hdfs-site.xml";
    public static final String HADOOP_CORE_SITE_XML = "hadoop.core-site.xml";
    public static final String DEFAULTS_DIR_URI = "defaults.dir.uri";
    /**
     * Optional target properties
     */
    public static final String HIVE_JDBC_URL = "hive.jdbc.url";

    private JScpWorker worker;

    public CelosCiTargetParser(String userName) throws FileSystemException {
        this.worker = new JScpWorker(userName);
    }

    private String getNotNull(HashMap<String, String> map, String key) {
        String result = map.get(key);
        if (result == null) {
            throw new IllegalStateException(key + " was not specified in the target file");
        }
        return result;
    }

    public CelosCiTarget parse(URI targetFileUri) throws Exception {
        FileObject file = worker.getFileObjectByUri(targetFileUri);
        InputStream is = file.getContent().getInputStream();
        HashMap<String, String> result = Util.JSON_READER.withType(HashMap.class).readValue(is);

        URI hdfsSiteXml = URI.create(getNotNull(result, HADOOP_HDFS_SITE_XML));
        URI hdfsCoreXml = URI.create(getNotNull(result, HADOOP_CORE_SITE_XML));
        URI workflowDir = URI.create(getNotNull(result, WORKFLOWS_DIR_URI));
        URI defaultsFile = URI.create(getNotNull(result, DEFAULTS_DIR_URI));

        URI hiveJdbc = result.get(HIVE_JDBC_URL) != null ? URI.create(result.get(HIVE_JDBC_URL)) : null;

        return new CelosCiTarget(hdfsSiteXml, hdfsCoreXml, workflowDir, defaultsFile, hiveJdbc);
    }

}
