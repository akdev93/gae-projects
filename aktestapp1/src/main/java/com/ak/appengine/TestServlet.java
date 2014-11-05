package com.ak.appengine;


import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by akoneri on 10/20/14.
 */
public class TestServlet extends HttpServlet{
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        System.out.println("Called doGet");
        resp.setContentType("text/html");
        resp.getWriter().println("<html><body><h1>hello world "+(new java.util.Date())+"</h1></body></html>");
    }
}
