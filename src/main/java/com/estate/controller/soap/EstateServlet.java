package com.estate.controller.soap;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

/**
 * The web servlet that initiates the soap service.
 */
@WebServlet(name = "estateServlet", value = "/estate-servlet")
public class EstateServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Estate Application";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}