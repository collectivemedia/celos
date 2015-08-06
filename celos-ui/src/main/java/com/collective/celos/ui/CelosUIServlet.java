package com.collective.celos.ui;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.collective.celos.CelosClient;
import com.collective.celos.Util;

/**
 * Created by akonopko on 22.07.15.
 */
public class CelosUIServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String celosURL = Util.requireNonNull(getServletContext().getAttribute(Main.CELOS_URL_ATTR).toString());
            CelosClient client = new CelosClient(celosURL);
            IOUtils.write("Number of workflows: " + client.getWorkflowList().size(), res.getOutputStream());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
}
