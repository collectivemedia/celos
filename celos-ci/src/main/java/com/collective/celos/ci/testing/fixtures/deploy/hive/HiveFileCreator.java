package com.collective.celos.ci.testing.fixtures.deploy.hive;

import com.collective.celos.ci.mode.test.TestRun;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by akonopko on 16.01.15.
 */
public abstract class HiveFileCreator {

    public abstract File create(TestRun testRun) throws IOException;

    public static class PlainHiveFileCreator extends HiveFileCreator {

        private final String relativePath;

        public PlainHiveFileCreator(String relativePath) {
            this.relativePath = relativePath;
        }

        public File create(TestRun testRun) throws IOException {
            return new File(testRun.getTestCasesDir(), relativePath);
        }
    }


    public static class ContentHiveFileCreator extends HiveFileCreator {

        private final String[][] cellData;

        public ContentHiveFileCreator(String[][] cellData) {
            this.cellData = cellData;
        }

        public File create(TestRun testRun) throws IOException {
            File hiveLoadFile = new File(testRun.getCelosTempDir(), "" + System.currentTimeMillis());
            FileOutputStream hiveLoadOS = new FileOutputStream(hiveLoadFile);
            for (String[] row : cellData) {
                IOUtils.write(StringUtils.join(row, "\t") + "\n", hiveLoadOS);
            }
            hiveLoadOS.close();
            return hiveLoadFile;
        }

        public String[][] getCellData() {
            return cellData;
        }
    }
}
