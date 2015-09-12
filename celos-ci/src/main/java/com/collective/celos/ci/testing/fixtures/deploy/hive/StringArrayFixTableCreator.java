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
package com.collective.celos.ci.testing.fixtures.deploy.hive;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 16.01.15.
 */
public class StringArrayFixTableCreator implements FixObjectCreator<FixTable> {

    private final String[] columnNames;
    private final String[][] data;

    public StringArrayFixTableCreator(String[] columnNames, String[][] data) {
        this.columnNames = columnNames;
        this.data = data;
    }

    @Override
    public FixTable create(TestRun testRun) throws Exception {
        return createFixTable(columnNames, Lists.newArrayList(data));
    }

    public static FixTable createFixTable(String[] columnNames, List<String[]> data) {
        List<FixTable.FixRow> fixRows = Lists.newArrayList();
        for (String[] rowData : data) {
            Map<String, String> rowContent = Maps.newHashMap();
            for (int i = 0; i < columnNames.length; i++) {
                rowContent.put(columnNames[i], rowData[i]);
            }
            fixRows.add(new FixTable.FixRow(rowContent));
        }
        FixTable fixTable = new FixTable(Lists.newArrayList(columnNames), fixRows);
        return fixTable;
    }

    @Override
    public String getDescription(TestRun testRun) {
        return "FixTable out of String[][]";
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String[][] getData() {
        return data;
    }
}
