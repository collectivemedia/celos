package com.collective.celos.ci.testing.fixtures.deploy.hive;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by akonopko on 16.01.15.
 */
public class FileFixTableCreator implements FixObjectCreator<FixTable> {

    private final File dataFile;
    private final Character separator;
    private final Character quoteChar;
    private final Character escapeChar;

    public FileFixTableCreator(File dataFile) {
        this(dataFile, '\t');
    }

    public FileFixTableCreator(File dataFile, Character separator) {
        this(dataFile, separator, CSVParser.DEFAULT_QUOTE_CHARACTER);
    }

    public FileFixTableCreator(File dataFile, Character separator, Character quoteChar) {
        this(dataFile, separator, quoteChar, CSVParser.DEFAULT_ESCAPE_CHARACTER);
    }

    public FileFixTableCreator(File dataFile, Character separator, Character quoteChar, Character escapeChar) {
        this.dataFile = dataFile;
        this.separator = separator;
        this.quoteChar = quoteChar;
        this.escapeChar = escapeChar;
    }

    @Override
    public FixTable create(TestRun testRun) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(dataFile), separator, quoteChar, escapeChar);
        List<String[]> myEntries = reader.readAll();
        String[] colNames = myEntries.get(0);
        List<String[]> rowData = myEntries.subList(1, myEntries.size() - 1);
        return StringArrayFixTableCreator.createFixTable(colNames, rowData);
    }

    @Override
    public String getDescription(TestRun testRun) {
        return "FixTable out of " + dataFile.getAbsolutePath();
    }

    public File getDataFile() {
        return dataFile;
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
