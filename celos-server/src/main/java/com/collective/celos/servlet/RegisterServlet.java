/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.servlet;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import com.collective.celos.database.StateDatabaseConnection;
import com.fasterxml.jackson.databind.JsonNode;

public class RegisterServlet extends AbstractJSONServlet {
    
    private static final String BUCKET_PARAM = "bucket";
    private static final String KEY_PARAM = "key";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            BucketID bucket = getRequestBucketID(req);
            RegisterKey key = getRequestKey(req);
            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
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
            JsonNode value = Util.JSON_READER.readTree(new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                connection.putRegister(bucket, key, value);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        BucketID bucket = getRequestBucketID(req);
        RegisterKey key = getRequestKey(req);
        try {
            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
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
    
}
