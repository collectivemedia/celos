package com.collective.celos.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

/**
 * Superclass for all servlets that access the database.
 * 
 * Serializes all database accesses with a lock.
 */
@SuppressWarnings("serial")
public abstract class AbstractServlet extends HttpServlet {

    /**
     * This lock serves to synchronize all operations.
     */
    protected static final Object LOCK = new Object();

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, java.io.IOException {
        synchronized(LOCK) {
            super.service(req, res);
        }
    }

}
