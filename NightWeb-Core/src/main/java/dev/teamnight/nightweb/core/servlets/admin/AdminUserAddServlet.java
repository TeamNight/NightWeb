/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.Group;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.GroupService;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminUserAddServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canCreateUsers")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		GroupService gserv = ctx.getServiceManager().getService(GroupService.class);
		List<Group> groups = gserv.getAll();
		
		ctx.getTemplate("admin/userCreate.tpl")
			.assign("currentUser", auth.getUser())
			.assign("groups", groups)
			.send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canCreateUsers")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		String username = req.getParameter("username");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		
		boolean fail = false;
		List<String> errors = new ArrayList<String>();
		
		EmailValidator emailVal = EmailValidator.getInstance();
		
		if(!emailVal.isValid(email)) {
			fail = true;
			errors.add("emailInvalidError");
		}
		
		if(!username.matches("[A-Za-zäöüÄÖÜß_-]{1,50}")) {
			fail = true;
			errors.add("usernameInvalidError");
		}
		
		if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&#])[A-Za-z0-9@$!%*?&#]{8,100}$")) {
			fail = true;
			errors.add("passwordInvalidError");
		}
		
		if(fail) {
			ctx.getTemplate("admin/userCreate.tpl").assign("failed", true).assign("errors", errors).send(resp);
			return;
		}
		
		UserService userv = ctx.getServiceManager().getService(UserService.class);
		
		if(userv.getByUsername(username) != null) {
			errors.add("usernameTakenError");
			ctx.getTemplate("admin/userCreate.tpl").assign("failed", true).assign("errors", errors).send(resp);
			return;
		} 
		
		if(userv.getByEmail(email) != null) {
			errors.add("emailTakenError");
			ctx.getTemplate("admin/userCreate.tpl").assign("failed", true).assign("errors", errors).send(resp);
			return;
		}
		
		User user = new User(username, email);
		user.setPassword(user.createHash(password));
		user.setActivationKey(StringUtil.getRandomString(16));
		
		GroupService gserv = ctx.getServiceManager().getService(GroupService.class);
		List<Group> groups = gserv.getAll();
		
		for(Group group : groups) {
			String groupParam = req.getParameter("groupMembership-" + group.getId());
			
			if(groupParam == null || groupParam.equalsIgnoreCase("false")) {
				continue;
			}
			
			if(!auth.getUser().canEdit(group)) {
				continue;
			}
			
			LogManager.getLogger().debug("Edit:Adding group: " + group);
			user.addGroup(group);
		}
		
		long id = (long) userv.create(user);
		
		ctx.getTemplate("admin/userCreate.tpl")
			.assign("currentUser", auth.getUser())
			.assign("groups", groups)
			.assign("user", user)
			.assign("userId", id)
			.assign("created", true)
			.send(resp);
	}
}
