package com.collective.celos.servlet;

import com.collective.celos.server.ServerConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Posting to this servlet clears the scheduler cache.
 */
@SuppressWarnings("serial")
public class ClearCacheServlet extends AbstractServlet {

    public ClearCacheServlet(ServerConfig celosServer) {
        super(celosServer);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        clearSchedulerCache();
    }

}
