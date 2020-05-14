/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.EmailValidator;

import dev.teamnight.nightweb.core.AdminSession;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.WebSession;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.UserService;

/**
 * @author Jonas
 *
 */
public class AdminLoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		AdminSession session = WebSession.getSession(req, AdminSession.class);
		
		if(session != null && session.isLoggedIn()) {
			resp.sendRedirect(ctx.getContextPath() + "/admin");
		}
		
		//Build the template
		ctx.getTemplate("admin/login.tpl").send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		AdminSession session = WebSession.getSession(req, AdminSession.class);
		
		if(session != null && session.isLoggedIn()) {
			resp.sendRedirect(ctx.getContextPath() + "/admin");
		}
		
		//Check parameters
		String emailOrUsername = req.getParameter("emailOrUsername");
		String password = req.getParameter("password");
		
		if(emailOrUsername == null || password == null) {
			ctx.getTemplate("admin/login.tpl").assign("failed", "userOrPasswordNullError").send(resp);
			return;
		}
		
		EmailValidator emailVal = EmailValidator.getInstance();
		
		boolean email = false;
		
		if(!emailVal.isValid(emailOrUsername)) {
			if(!emailOrUsername.matches("[A-Za-zäöüÄÖÜß_-]{1,50}")) {
				ctx.getTemplate("admin/login.tpl").assign("failed", "unknownUserError").send(resp);
				return;
			}
		} else {
			email = true;
		}
		
		UserService userServ = ctx.getServiceManager().getService(UserService.class);
		
		User user;
		if(email) {
			user = userServ.getByEmail(emailOrUsername);
		} else {
			user = userServ.getByUsername(emailOrUsername);
		}
		
		//Check if user has permission to enter ACP
		if(!user.hasPermission("nightweb.admin.canUseACP")) {
			ctx.getTemplate("admin/login.tpl").assign("failed", "notAdminError").send(resp);
			return;
		}
		
		//Check user password
		if(!user.getPassword().equals(user.createHash(password))) {
			ctx.getTemplate("admin/login.tpl").assign("failed", "invalidPasswordError").send(resp);
			return;
		}
		
		if(session == null) {
			session = new AdminSession(ctx);
		}
		session.setUser(user);
		req.getSession(true).setAttribute("session", session);
		
		resp.sendRedirect(ctx.getContextPath() + "admin");
	}

}
