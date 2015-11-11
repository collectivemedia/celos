package com.collective.celos.trigger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by akonopko on 11.11.15.
 */
public class FSThreadLocal {

    public static final ThreadLocal<Map<String, FileSystem>> userThreadLocal = new ThreadLocal();

    public static FileSystem getOrCreate(String fsPath) throws Exception {
        Map<String, FileSystem> map = getMap(fsPath);
        FileSystem fileSystem = map.get(fsPath);
        if (fileSystem == null) {
            fileSystem = createFileSystem(fsPath);
            map.put(fsPath, fileSystem);
        }
        return fileSystem;
    }

    private static Map<String, FileSystem> getMap(String fsPath) {
        Map<String, FileSystem> map = userThreadLocal.get();
        if (map == null) {
            map = new ConcurrentHashMap<>();
            userThreadLocal.set(map);
        }
        return map;
    }

    private static FileSystem createFileSystem(String fsString) throws Exception {
        Configuration conf = new Configuration();
        //required due http://stackoverflow.com/questions/17265002/hadoop-no-filesystem-for-scheme-file
        conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

        addFileToConfiguration(conf, "hdfs-site.xml");
        addFileToConfiguration(conf, "core-site.xml");

        return FileSystem.get(new URI(fsString), conf);
    }

    private static void addFileToConfiguration(Configuration conf, String fileName) throws Exception {
        InputStream fileStream = FSThreadLocal.class.getClassLoader().getResourceAsStream(fileName);
        if (fileStream != null) {
            conf.addResource(fileStream);
        }
    }

}
