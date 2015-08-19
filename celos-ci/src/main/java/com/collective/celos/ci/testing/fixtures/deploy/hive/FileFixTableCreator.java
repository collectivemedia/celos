package com.collective.celos.ci.testing.fixtures.deploy.hive;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

import com.collective.celos.Util;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;

/**
 * Created by akonopko on 16.01.15.
 */
public class FileFixTableCreator implements FixObjectCreator<FixTable> {

    private final FixObjectCreator<FixFile> fileCreator;
    private final Character separator;

    public FileFixTableCreator(FixObjectCreator<FixFile> fileCreator) {
        this(fileCreator, '\t');
    }
    
    public FileFixTableCreator(FixObjectCreator<FixFile> fileCreator, Character separator) {
        this.fileCreator = Util.requireNonNull(fileCreator);
        this.separator = Util.requireNonNull(separator);
    }

    @Override
    public FixTable create(TestRun testRun) throws Exception {
        try (CSVReader reader = new CSVReader(getFileReader(testRun), separator, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER)) {
            List<String[]> myEntries = reader.readAll();
            String[] colNames = myEntries.get(0);
            List<String[]> rowData = myEntries.subList(1, myEntries.size());
            return StringArrayFixTableCreator.createFixTable(colNames, rowData);
        }
    }

    private Reader getFileReader(TestRun testRun) throws Exception {
        return new InputStreamReader(fileCreator.create(testRun).getContent());
    }

    @Override
    public String getDescription(TestRun testRun) throws Exception {
        return "FixTable out of " + fileCreator.getDescription(testRun);
    }

}
