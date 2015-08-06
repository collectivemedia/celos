package com.collective.celos.ui;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by akonopko on 22.07.15.
 */
public class CelosUIServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        IOUtils.write(getServletContext().getAttribute(Main.CELOS_URL_ATTR).toString(), res.getOutputStream());
    }
    
}
