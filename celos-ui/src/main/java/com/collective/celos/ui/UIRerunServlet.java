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
