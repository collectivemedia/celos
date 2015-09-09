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
package com.collective.celos.ui;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.CelosClient;
import com.collective.celos.SlotID;
import com.collective.celos.Util;

/**
 * Called from the browser to rerun a slot.
 */
public class UIRerunServlet extends HttpServlet {
    
    private static final String SLOT_ID = "id";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            String idStr = req.getParameter(SLOT_ID);
            if (idStr == null) {
                throw new IllegalArgumentException("id parameter must be specified");
            }
            SlotID id = SlotID.fromString(idStr);
            URL celosURL = (URL) Util.requireNonNull(getServletContext().getAttribute(Main.CELOS_URL_ATTR));
            CelosClient client = new CelosClient(celosURL.toURI());
            client.rerunSlot(id);
            res.setStatus(HttpServletResponse.SC_OK);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }
}
