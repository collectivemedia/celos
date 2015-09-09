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

        private final Map<Path, String> messages = Maps.newLinkedHashMap();

        @Override
        public void process(Path path, FixObjectCompareResult ff) throws IOException {
            if (StringUtils.isNotEmpty(ff.message)) {
                messages.put(path, ff.message);
            }
        }
    }

}
