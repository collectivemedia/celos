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

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import com.collective.celos.database.StateDatabaseConnection;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class RegisterServlet extends AbstractJSONServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            BucketID bucket = getRequestBucketID(req);
            Set<RegisterKey> keys = getRequestKeys(req);
            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                Map<RegisterKey, JsonNode> value = connection.getRegisters(bucket, keys);
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
            JsonNode payload = Util.JSON_READER.readTree(new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));

            Map<RegisterKey, JsonNode> keyValues = Maps.newHashMap();
            payload.fields().forEachRemaining(x -> keyValues.put(new RegisterKey(x.getKey()), x.getValue()));

            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                connection.putRegisters(bucket, keyValues);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        BucketID bucket = getRequestBucketID(req);
        Set<RegisterKey> keys = getRequestKeys(req);
        try {
            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                connection.deleteRegisters(bucket, keys);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    private BucketID getRequestBucketID(HttpServletRequest req) {
        return new BucketID(req.getParameter(CelosClient.BUCKET_PARAM));
    }

    private Set<RegisterKey> getRequestKeys(HttpServletRequest req) {
        Set<RegisterKey> keys = Sets.newHashSet();
        for(String key: req.getParameter(CelosClient.KEYS_PARAM).split(CelosClient.KEYS_DELIMITER)) {
            keys.add(new RegisterKey(key));
        }
        return keys;
    }

}
