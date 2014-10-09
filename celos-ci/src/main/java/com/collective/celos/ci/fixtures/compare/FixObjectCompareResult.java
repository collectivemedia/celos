package com.collective.celos.ci.fixtures.compare;

import com.google.common.collect.Maps;
import org.apache.commons.lang.text.StrBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 10/8/14.
 */
public class FixObjectCompareResult {

    private final Map<String, FixObjectCompareResult> children;
    private final String message;
    private final Status status;
    private static final FixObjectCompareResult SUCCESS_STATUS = new FixObjectCompareResult(Collections.EMPTY_MAP, "", Status.SUCCESS);

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

    public static FixObjectCompareResult success() {
        return SUCCESS_STATUS;
    }

    public static FixObjectCompareResult wrapFailed(Map<String, FixObjectCompareResult> child) {
        return new FixObjectCompareResult(child, "", Status.FAIL);
    }

    public Map<String, FixObjectCompareResult> getChildren() {
        return children;
    }

    public String getMessage() {
        return message;
    }

    public Status getStatus() {
        return status;
    }

    public String generateDescription() {
        StrBuilder strBuilder = new StrBuilder();
        for (Map.Entry<Path, String> pathMessage : processCompareResult(this).entrySet()) {
            strBuilder.appendln(pathMessage.getKey());
            strBuilder.appendln(pathMessage.getValue());
            strBuilder.appendNewLine();
        }
        return strBuilder.toString();
    }

    private Map<Path, String> processCompareResult(FixObjectCompareResult compareResult) {
        Map<Path, String> result = Maps.newHashMap();
        for (Map.Entry<String, FixObjectCompareResult> child : compareResult.getChildren().entrySet()) {
            Map<Path, String> pathToMessages = processCompareResult(child.getValue());
            for (Map.Entry<Path, String> pathToMessage : pathToMessages.entrySet()) {
                result.put(Paths.get(child.getKey(), pathToMessage.getKey().toString()), pathToMessage.getValue());
            }
        }
        return result;
    }

}
