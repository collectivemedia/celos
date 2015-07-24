package com.collective.celos.servlet;

import com.collective.celos.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@SuppressWarnings("serial")
public class UIConfigServlet extends AbstractJSONServlet {

    private final static String CONFIG_FILE = "config.json";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            String configPath = getServletContext().getInitParameter(Constants.UI_PATH_ATTR);
            IOUtils.copy(FileUtils.openInputStream(new File(configPath, CONFIG_FILE)), res.getOutputStream());
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }
    
}
