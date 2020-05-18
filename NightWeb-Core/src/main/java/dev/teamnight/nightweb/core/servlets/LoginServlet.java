/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.EmailValidator;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.entities.Setting;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.SettingService;
import dev.teamnight.nightweb.core.service.UserService;

/**
 * @author Jonas
 *
 */
public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(auth.isAuthenticated()) {
			resp.sendRedirect("/");
		}
		
		//Build the template
		ctx.getTemplate("login.tpl").send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(auth.isAuthenticated()) {
			resp.sendRedirect("/");
		}
		
		//Check parameters
		String emailOrUsername = req.getParameter("emailOrUsername");
		String password = req.getParameter("password");
		
		if(emailOrUsername == null || password == null) {
			ctx.getTemplate("login.tpl").assign("failed", "userOrPasswordNullError").send(resp);
			return;
		}
		
		EmailValidator emailVal = EmailValidator.getInstance();
		
		boolean email = false;
		
		if(!emailVal.isValid(emailOrUsername)) {
			if(!emailOrUsername.matches("[A-Za-zäöüÄÖÜß_-]{1,50}")) {
				ctx.getTemplate("login.tpl").assign("failed", "unknownUserError").send(resp);
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
		
		//Check if login is enabled
		SettingService setServ = ctx.getServiceManager().getService(SettingService.class);
		Setting loginEnabled = setServ.getByKey("loginEnabled");
		
		if(!loginEnabled.getAsBoolean() && !user.hasPermission("nightweb.core.admin.canBypassDisabledLogin")) {
			ctx.getTemplate("login.tpl").assign("loginDisabled", true).send(resp);
			return;
		}
		
		if(user.isDisabled()) {
			ctx.getTemplate("login.tpl").assign("failed", "userDisabledError").send(resp);
			return;
		}
		
		
		//Check user password
		auth.setUser(user);
		
		if(!auth.authenticate(password)) {
			ctx.getTemplate("login.tpl").assign("failed", "invalidPasswordError").send(resp);
			return;
		}
		
		resp.sendRedirect("/");
		
		//TODO add logging for login
	}
	
}
