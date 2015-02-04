package com.collective.celos.ci.testing.fixtures.deploy.hive;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixTable;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by akonopko on 16.01.15.
 */
public class FileFixTableCreator implements FixObjectCreator<FixTable> {

    private final Path relativePath;
    private final Character separator;

    public FileFixTableCreator(Path relativePath) {
        this(relativePath, '\t');
    }

    public FileFixTableCreator(Path relativePath, Character separator) {
        this.relativePath = relativePath;
        this.separator = separator;
    }

    @Override
    public FixTable create(TestRun testRun) throws Exception {
        File dataFile = getCsvFilePath(testRun);
        CSVReader reader = new CSVReader(new FileReader(dataFile), separator, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER);
        List<String[]> myEntries = reader.readAll();
        String[] colNames = myEntries.get(0);
        List<String[]> rowData = myEntries.subList(1, myEntries.size());
        return StringArrayFixTableCreator.createFixTable(colNames, rowData);
    }

    private File getCsvFilePath(TestRun testRun) {
        return new File(testRun.getTestCasesDir(), relativePath.toString());
    }

    @Override
    public String getDescription(TestRun testRun) {
        return "FixTable out of " + getCsvFilePath(testRun);
    }

    public Path getRelativePath() {
        return relativePath;
    }

}
