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

import com.collective.celos.BucketID;
import com.collective.celos.CelosClient;
import com.collective.celos.RegisterKey;
import com.collective.celos.Util;
import com.collective.celos.database.StateDatabaseConnection;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class RegisterKeysServlet extends AbstractJSONServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            BucketID bucket = new BucketID(req.getParameter(CelosClient.BUCKET_PARAM));
            String prefix = req.getParameter(CelosClient.PREFIX_PARAM);

            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                Set<RegisterKey> keys = connection.getRegisterKeys(bucket, prefix);
                ObjectNode object = Util.MAPPER.createObjectNode();
                ArrayNode list = Util.MAPPER.createArrayNode();
                object.put(CelosClient.KEYS_FIELD, list);
                keys.stream().sorted().forEach(x -> list.add(x.toString()));
                writer.writeValue(res.getOutputStream(), object);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}
