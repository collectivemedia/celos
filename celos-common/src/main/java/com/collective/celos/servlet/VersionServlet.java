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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is used in production to verify that the running instance 
 * was started from the latest Git commit.
 */
@SuppressWarnings("serial")
public class VersionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            final String serviceVersion = System.getenv("CELOS_VERSION");
            if (serviceVersion != null) {
                res.getOutputStream().write(serviceVersion.getBytes());
            } else {
                throw new Exception("CELOS_VERSION is undefined");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
