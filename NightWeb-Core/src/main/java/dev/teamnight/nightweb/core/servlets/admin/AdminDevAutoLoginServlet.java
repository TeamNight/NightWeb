/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jonas
 *
 */
public class AdminDevAutoLoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getSession().setAttribute("dev.teamnight.nightweb.core.session.userId", 4L);
		req.getSession().setAttribute("dev.teamnight.nightweb.core.session.adminSession", true);
		
		resp.sendRedirect("/admin/users");
	}
	
}
