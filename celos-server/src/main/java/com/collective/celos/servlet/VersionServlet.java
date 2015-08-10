package com.collective.celos.servlet;

import com.collective.celos.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class VersionServlet extends AbstractJSONServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            String version = getServletContext().getInitParameter(Constants.VERSION_ATTR);
            res.getOutputStream().write(version.getBytes());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
