package com.collective.celos.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.BucketID;
import com.collective.celos.RegisterKey;
import com.collective.celos.Scheduler;
import com.collective.celos.StateDatabaseConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class RegisterServlet extends AbstractJSONServlet {
    
    private static final String BUCKET_PARAM = "bucket";
    private static final String KEY_PARAM = "key";
    private static final String VALUE_PARAM = "value";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            BucketID bucket = getRequestBucketID(req);
            RegisterKey key = getRequestKey(req);
            Scheduler scheduler = getOrCreateCachedScheduler();
            try(StateDatabaseConnection connection = scheduler.getStateDatabase().openConnection()) {
                JsonNode value = connection.getRegister(bucket, key);
                if (value == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Register not found");
                } else {
                    writer.writeValue(res.getOutputStream(), value);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            BucketID bucket = getRequestBucketID(req);
            RegisterKey key = getRequestKey(req);
            JsonNode value = getRequestValue(req);
            Scheduler scheduler = getOrCreateCachedScheduler();
            try(StateDatabaseConnection connection = scheduler.getStateDatabase().openConnection()) {
                connection.putRegister(bucket, key, value);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        BucketID bucket = getRequestBucketID(req);
        RegisterKey key = getRequestKey(req);
        Scheduler scheduler;
        try {
            scheduler = getOrCreateCachedScheduler();
            try(StateDatabaseConnection connection = scheduler.getStateDatabase().openConnection()) {
                connection.deleteRegister(bucket, key);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    private BucketID getRequestBucketID(HttpServletRequest req) {
        return new BucketID(req.getParameter(BUCKET_PARAM));
    }
    
    private RegisterKey getRequestKey(HttpServletRequest req) {
        return new RegisterKey(req.getParameter(KEY_PARAM));
    }
    
    private JsonNode getRequestValue(HttpServletRequest req) throws JsonProcessingException, IOException {
        return mapper.readTree(req.getParameter(VALUE_PARAM));
    }
    
}
