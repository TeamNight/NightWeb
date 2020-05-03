package dev.teamnight.nightweb.core.servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.teamnight.nightweb.core.Authenticated;

@Authenticated
public class TestServlet extends HttpServlet {
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			throw new RuntimeException("Test");
		} catch(RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException
    {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println("<h1>Hello from HelloServlet</h1>");
    }
}
