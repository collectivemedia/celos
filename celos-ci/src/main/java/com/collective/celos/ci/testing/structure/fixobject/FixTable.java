package com.collective.celos.ci.testing.structure.fixobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public static class FixRow {

        private final Map<String, String> cells;

        public FixRow(Map<String, String> cells) {
            for (Map.Entry<String, String> cell: cells.entrySet()) {
                if (cell.getValue() == null || cell.getKey() == null) {
                    throw new IllegalStateException("Cannot create FixRow out of Map that contains nulls");
                }
            }
            this.cells = cells;
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
