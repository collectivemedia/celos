package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.fixtures.CelosResultsCompareException;
import com.collective.celos.ci.fixtures.processor.TraverseFileProcessor;
import com.collective.celos.ci.fixtures.structure.FixDir;
import com.collective.celos.ci.fixtures.structure.FixFile;
import com.collective.celos.ci.fixtures.processor.FileObjectTreeProcessor;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 10/7/14.
 */
public class PlainFileComparer implements FixDirComparer {

    public void compare(FixDir expectedDirTree, FixDir actualDirTree) throws Exception {
        Set<Path> visited = Sets.newHashSet();

        Map<Path, FixFile> expected = getPathFixFileMap(expectedDirTree);
        Map<Path, FixFile> results = getPathFixFileMap(actualDirTree);

        for (Map.Entry<Path, FixFile> exp : expected.entrySet()) {
            FixFile resFixFile = results.get(exp.getKey());
            if (resFixFile == null) {
                throw new CelosResultsCompareException("File " + exp.getValue() + " was not found among actual result set");
            }
            if (!IOUtils.contentEquals(exp.getValue().getContent(), resFixFile.getContent())) {
                throw new CelosResultsCompareException("File " + exp.getValue() + " differs from the expected file");
            }
            visited.add(exp.getKey());
        }
        if (!results.isEmpty()) {
            Set<Path> unvisited = Sets.difference(results.keySet(), visited);
            StringBuffer stringBuffer = new StringBuffer();
            for (Path un : unvisited) {
                if (stringBuffer.length() > 0) {
                    stringBuffer.append(", ");
                }
                stringBuffer.append(results.get(un));
            }
            throw new CelosResultsCompareException("Files [" + stringBuffer.toString() + "] were not found among expected result set");
        }

    }

    private Map<Path, FixFile> getPathFixFileMap(FixDir dirTree) throws IOException {
        TraverseFileProcessor expectedTrav = new TraverseFileProcessor();
        new FileObjectTreeProcessor().process(dirTree, expectedTrav);
        return expectedTrav.getTraversedFiles();
    }


}
