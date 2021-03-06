/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.annotations.AdminServlet;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminDashboardServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		//Build the template
		ctx.getTemplate("admin/dashboard.tpl")
			.assign("currentUser", auth.getUser())
			.assign("implementationName", NightWeb.getCoreApplication().getImplementationName())
			.assign("version", NightWeb.getCoreApplication().getVersion())
			.send(resp);
	}
	
}
