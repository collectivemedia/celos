package com.collective.celos.servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Returns static HTML which runs User Interface JS App
 */
@SuppressWarnings("serial")
public class UIServlet extends AbstractJSONServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        res.setContentType("text/html");

        try {
            File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/celos-ui.html").toURI());
            IOUtils.copy(new FileInputStream(src), res.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
