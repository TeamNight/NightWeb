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

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.StringUtil;
import dev.teamnight.nightweb.core.WebSession;
import dev.teamnight.nightweb.core.entities.Group;
import dev.teamnight.nightweb.core.entities.Setting;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.GroupService;
import dev.teamnight.nightweb.core.service.SettingService;
import dev.teamnight.nightweb.core.service.UserService;

/**
 * @author Jonas
 *
 */
public class RegistrationServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		WebSession session = WebSession.getSession(req);
		
		if(session.isLoggedIn()) {
			resp.sendRedirect("/");
		}
		
		Context ctx = Context.get(req);
		
		//Check if registration is enabled
		SettingService setServ = ctx.getServiceManager().getService(SettingService.class);
		Setting regEnabled = setServ.getByKey("registrationEnabled");
		
		if(!regEnabled.getAsBoolean()) {
			ctx.getTemplate("register.tpl").assign("regDisabled", true).send(resp);
			return;
		}
		
		//Build the template
		ctx.getTemplateManager().builder("register.tpl").send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//Checking session and getting context
		WebSession session = WebSession.getSession(req);
		
		if(session.isLoggedIn()) {
			resp.sendRedirect("/");
		}
		
		Context ctx = Context.get(req);
		
		//Check if registration is enabled
		SettingService setServ = ctx.getServiceManager().getService(SettingService.class);
		Setting regEnabled = setServ.getByKey("registrationEnabled");
		
		if(!regEnabled.getAsBoolean()) {
			ctx.getTemplate("register.tpl").assign("regDisabled", true).send(resp);
			return;
		}
		
		//Checking input parameters
		String email = req.getParameter("email");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String password2 = req.getParameter("confirmPassword");
		
		boolean fail = false;
		StringBuilder builder = new StringBuilder();
		builder.append("The following problems occured: \n<ul>");
		
		EmailValidator emailVal = EmailValidator.getInstance();
		
		if(!emailVal.isValid(email)) {
			fail = true;
			builder.append("<li>Your email is not valid.</li>");
		}
		
		if(!username.matches("[A-Za-zäöüÄÖÜß_-]{1,50}")) {
			fail = true;
			builder.append("<li>Your username is not valid. Valid are all alphanumeric symbols as well as ä,ö,ü,ß,_,-.</li>");
		}
		
		if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&#])[A-Za-z0-9@$!%*?&#]{8,100}$")) {
			fail = true;
			builder.append("<li>Your password has to be at least 8 characters long and contain uppercase and lowercase letters, one number and one special character.</li>");
		}
		
		if(!password2.equals(password)) {
			fail = true;
			builder.append("<li>Your password confirmatind does not match your password.");
		}
		
		if(fail) {
			ctx.getTemplate("register.tpl").withErrorMessage(builder.append("</ul>").toString()).send(resp);
			return;
		}
		
		UserService userService = ctx.getServiceManager().getService(UserService.class);
		
		if(userService.getByUsername(username) != null) {
			ctx.getTemplate("register.tpl").withErrorMessage("This username is taken.").send(resp);
			return;
		} else if(userService.getByEmail(email) != null) {
			ctx.getTemplate("register.tpl").withErrorMessage("This email is taken.").send(resp);
			return;
		}
		
		//Set user fields
		User user = new User(username, email);
		user.setPassword(user.createHash(password));
		user.setActivationKey(StringUtil.getRandomString(16));
		
		//Set default group
		long groupId = ctx.getServiceManager().getService(SettingService.class).getByKey("defaultGroup").getAsLong();
		
		if(groupId > 0) {
			Group group = ctx.getServiceManager().getService(GroupService.class).getOne(groupId);
			user.getGroups().add(group);
		}
		
		userService.create(user);
		ctx.getTemplate("register.tpl").assign("regComplete", true).send(resp);
		
		//TODO send mail
	}
}
