
package com.collective.celos;

import org.junit.Test;
import org.mozilla.javascript.EvaluatorException;

import java.io.File;
import java.io.FileReader;

public class JSConfigParserTest {

    @Test(expected = EvaluatorException.class)
    public void testCompileFails() throws Exception {
        JSConfigParser parser = new JSConfigParser();
        File file = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/read-js/incorrect-wf-js").toURI());

        parser.validateJsSyntax(new FileReader(file), file.getName());
    }

    @Test
    public void testCompilePasses() throws Exception {
        JSConfigParser parser = new JSConfigParser();
        File file = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/read-js/correct-wf-js").toURI());

        parser.validateJsSyntax(new FileReader(file), file.getName());
    }

}
