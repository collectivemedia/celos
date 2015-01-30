package com.collective.celos.ci.testing.fixtures.deploy.hive;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;

import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Created by akonopko on 16.01.15.
 */
public class FileFixTableCreator implements FixObjectCreator<FixTable> {

    private final String relativePath;
    private final Character separator;
    private final Character quoteChar;
    private final Character escapeChar;

    public FileFixTableCreator(String relativePath) {
        this(relativePath, '\t');
    }

    public FileFixTableCreator(String relativePath, Character separator) {
        this(relativePath, separator, CSVParser.DEFAULT_QUOTE_CHARACTER);
    }

    public FileFixTableCreator(String relativePath, Character separator, Character quoteChar) {
        this(relativePath, separator, quoteChar, CSVParser.DEFAULT_ESCAPE_CHARACTER);
    }

    public FileFixTableCreator(String relativePath, Character separator, Character quoteChar, Character escapeChar) {
        this.relativePath = relativePath;
        this.separator = separator;
        this.quoteChar = quoteChar;
        this.escapeChar = escapeChar;
    }

    @Override
    public FixTable create(TestRun testRun) throws Exception {
        File dataFile = new File(testRun.getTestCasesDir(), relativePath);
        CSVReader reader = new CSVReader(new FileReader(dataFile), separator, quoteChar, escapeChar);
        List<String[]> myEntries = reader.readAll();
        String[] colNames = myEntries.get(0);
        List<String[]> rowData = myEntries.subList(1, myEntries.size() - 1);
        return StringArrayFixTableCreator.createFixTable(colNames, rowData);
    }

    @Override
    public String getDescription(TestRun testRun) {
        return "FixTable out of " + relativePath;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public Character getSeparator() {
        return separator;
    }

    public Character getQuoteChar() {
        return quoteChar;
    }

    public Character getEscapeChar() {
        return escapeChar;
    }
}
