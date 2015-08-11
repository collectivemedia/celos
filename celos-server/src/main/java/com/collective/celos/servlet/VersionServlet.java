package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class VersionServlet extends AbstractJSONServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            res.getOutputStream().write(System.getenv("CELOS_VERSION").getBytes());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
