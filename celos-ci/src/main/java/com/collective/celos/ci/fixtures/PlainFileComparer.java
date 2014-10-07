package com.collective.celos.ci.fixtures;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 10/7/14.
 */
public class PlainFileComparer extends AbstractFileComparer {

    public PlainFileComparer(List<AbstractFileDataReader> fileDataReaders, List<AbstractFileDataReader> actualResultsReaders) {
        super(fileDataReaders, actualResultsReaders);
    }

    public void compare() throws Exception {
        Set<String> visited = Sets.newHashSet();

        Map<String, FileInfo> expected = getFileInfoMap(getActualResultsReaders());
        Map<String, FileInfo> results = getFileInfoMap(getExpectedResultsReaders());
        for (Map.Entry<String, FileInfo> exp : expected.entrySet()) {
            FileInfo resFileInfo = results.get(exp.getKey());
            if (resFileInfo == null) {
                throw new CelosResultsCompareException("File " + exp.getValue().getFullPath() + " was not found among actual result set");
            }
            if (!IOUtils.contentEquals(exp.getValue().getInputStream(), resFileInfo.getInputStream())) {
                throw new CelosResultsCompareException("File " + exp.getValue().getFullPath() + " differs from the expected file "  + resFileInfo.getFullPath());
            }
            visited.add(exp.getKey());
        }
        if (!results.isEmpty()) {
            Set<String> unvisited = Sets.difference(results.keySet(), visited);
            StringBuffer stringBuffer = new StringBuffer();
            for (String un : unvisited) {
                if (stringBuffer.length() > 0) {
                    stringBuffer.append(", ");
                }
                stringBuffer.append(results.get(un));
            }
            throw new CelosResultsCompareException("Files [" + stringBuffer.toString() + "] were not found among expected result set");
        }

    }

    private Map<String, FileInfo> getFileInfoMap(List<AbstractFileDataReader> r) throws Exception {
        Map<String, FileInfo> map = Maps.newHashMap();
        for (AbstractFileDataReader reader : r) {
            for (FileInfo fi : reader.read()) {
                map.put(fi.getRelativePath(), fi);
            }
        }
        return map;
    }
}
