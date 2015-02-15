package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.tree.TreeObjectProcessor;
import com.collective.celos.ci.testing.structure.tree.TreeObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

/**
 * Created by akonopko on 10/8/14.
 */
public class FixObjectCompareResult implements TreeObject<FixObjectCompareResult> {

    public static final FixObjectCompareResult SUCCESS = new FixObjectCompareResult(Collections.EMPTY_MAP, "", Status.SUCCESS);

    private final Map<String, FixObjectCompareResult> children;
    private final String message;
    private final Status status;

    public enum Status {
        FAIL, SUCCESS
    }

    public FixObjectCompareResult(Map<String, FixObjectCompareResult> children, String message, Status status) {
        this.children = children;
        this.message = message;
        this.status = status;
    }

    public static FixObjectCompareResult failed(String message) {
        return new FixObjectCompareResult(Collections.EMPTY_MAP, message, Status.FAIL);
    }

    public static FixObjectCompareResult wrapFailed(Map<String, FixObjectCompareResult> child, String message) {
        return new FixObjectCompareResult(child, message, Status.FAIL);
    }

    public Map<String, FixObjectCompareResult> getChildren() {
        return children;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String generateDescription() throws IOException {
        StrBuilder strBuilder = new StrBuilder();
        PathToMessageProcessor pathToMessage = new PathToMessageProcessor();
        TreeObjectProcessor.process(this, pathToMessage);
        for (Map.Entry<Path, String> pathMessage : pathToMessage.messages.entrySet()) {
            String keyText = StringUtils.isEmpty(pathMessage.getKey().toString()) ? "" : pathMessage.getKey() + " : " ;
            strBuilder.appendln(keyText + pathMessage.getValue());
        }
        return strBuilder.toString();
    }

    private static class PathToMessageProcessor extends TreeObjectProcessor<FixObjectCompareResult> {

        private final Map<Path, String> messages = Maps.newHashMap();

        @Override
        public void process(Path path, FixObjectCompareResult ff) throws IOException {
            if (StringUtils.isNotEmpty(ff.message)) {
                messages.put(path, ff.message);
            }
        }
    }

}
