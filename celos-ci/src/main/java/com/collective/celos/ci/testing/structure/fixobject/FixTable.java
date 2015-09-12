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
package com.collective.celos.ci.testing.structure.fixobject;

import com.collective.celos.ValueObject;

import java.util.*;

/**
 * Created by akonopko on 22.01.15.
 */
public class FixTable extends FixObject {

    private final List<String> columnNames;

    private final List<FixRow> rows;

    public FixTable(List<String> columnNames, List<FixRow> rows) {
        this.columnNames = columnNames;
        this.rows = rows;
    }

    public List<String> getColumnNames() {
        return Collections.unmodifiableList(columnNames);
    }

    public List<FixRow> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public static class FixRow extends ValueObject {

        private final Map<String, String> cells;

        public FixRow(Map<String, String> cells) {
            for (Map.Entry<String, String> cell: cells.entrySet()) {
                if (cell.getValue() == null || cell.getKey() == null) {
                    throw new IllegalStateException("Cannot create FixRow out of Map that contains nulls");
                }
            }
            this.cells = new TreeMap<>(cells);
        }

        public Map<String, String> getCells() {
            return Collections.unmodifiableMap(cells);
        }
        
        // Utility to get columns in the order they are declared in a table
        public List<String> getOrderedColumns(List<String> columnNames) {
            List<String> values = new ArrayList<String>(columnNames.size());
            for (String name : columnNames) {
                values.add(cells.get(name));
            }
            return values;
        }
    }

}
