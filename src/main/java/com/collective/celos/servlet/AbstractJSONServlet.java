package com.collective.celos.servlet;

import com.collective.celos.server.ServerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Utilities for JSON API servlets.
 */
@SuppressWarnings("serial")
public class AbstractJSONServlet extends AbstractServlet {

    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    public AbstractJSONServlet(ServerConfig celosServer) {
        super(celosServer);
    }
}
