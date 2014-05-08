package com.collective.celos.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Utilities for JSON API servlets.
 */
@SuppressWarnings("serial")
public abstract class AbstractJSONServlet extends AbstractServlet {

    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

}
