/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.annotations.Authenticated;

/**
 * @author Jonas
 *
 */
@Authenticated
public class LogoutServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Authenticator auth = Context.get(req).getAuthenticator(req.getSession());
		
		if(auth.isAuthenticated()) {
			auth.invalidate();
		}
		
		req.getSession().invalidate();
		
		resp.sendRedirect("/");
	}
	
}
