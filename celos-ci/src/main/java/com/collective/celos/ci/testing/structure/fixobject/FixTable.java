package com.collective.celos.ci.testing.structure.fixobject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 22.01.15.
 */
public class FixTable extends FixObject {

    public static class FixRow {

        private final Map<String, String> cells;

        public FixRow(Map<String, String> cells) {
            this.cells = cells;
        }

        public Map<String, String> getCells() {
            return Collections.unmodifiableMap(cells);
        }
    }

    private final List<FixRow> rows;

    public FixTable(List<FixRow> rows) {
        this.rows = rows;
    }

    public List<FixRow> getRows() {
        return Collections.unmodifiableList(rows);
    }

}
