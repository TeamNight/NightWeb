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
import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.util.URIUtil;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.StringUtil;
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
		
		LogManager.getLogger().debug("Session ID: " + req.getSession().getId());
		req.getSession().getAttributeNames().asIterator().forEachRemaining(string -> LogManager.getLogger().debug("Session attribute present: " + string));
		
		if(req.getSession().getAttribute("dev.teamnight.nightweb.core.session.adminSession") != null) {
			resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin"));
		}
		
		//Build the template
		ctx.getTemplate("admin/login.tpl").send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		
		if(req.getSession().getAttribute("dev.teamnight.nightweb.core.session.adminSession") != null) {
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
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		auth.setUser(user);
		
		if(!auth.authenticate(password)) {
			ctx.getTemplate("admin/login.tpl").assign("failed", "invalidPasswordError").send(resp);
			return;
		}
		
		req.getSession().setAttribute("dev.teamnight.nightweb.core.session.adminSession", true);
		
		LogManager.getLogger().debug("CTXPATH: " + ctx.getContextPath());
		LogManager.getLogger().debug("CTXPATH ENC: " + URIUtil.encodePath(ctx.getContextPath()));
		
		resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin"));
	}

}
