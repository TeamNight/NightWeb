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
import org.apache.commons.validator.routines.LongValidator;
import org.apache.logging.log4j.LogManager;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.Group;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.GroupService;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.util.PathParameters;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminUserEditServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canEditUsers")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		String sUserId = new PathParameters(req.getPathInfo()).getParameter(0);
		
		long userId = 0L;
		
		LongValidator val = LongValidator.getInstance();
		if(val.isValid(sUserId)) {
			userId = Long.parseLong(sUserId);
		}
		
		if(userId == 0L) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownUserError")
				.send(resp);
			return;
		}
		
		UserService userv = ctx.getServiceManager().getService(UserService.class);
		User user = userv.getOne(userId);
		
		if(user == null) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownUserError")
				.send(resp);
			return;
		}
		
		if(!auth.getUser().canEdit(user) && auth.getUser().getId() != user.getId()) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "insufficientPermissionError")
				.send(resp);
			return;
		}
		
		GroupService gserv = ctx.getServiceManager().getService(GroupService.class);
		List<Group> groups = gserv.getAll();
		
		ctx.getTemplate("admin/userEdit.tpl")
			.assign("currentUser", auth.getUser())
			.assign("user", user)
			.assign("groups", groups)
			.assign("saved", (req.getParameter("saved") != null ? "true" : "false"))
			.send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canEditUsers")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		String sUserId = new PathParameters(req.getPathInfo()).getParameter(0);
		
		long userId = 0L;
		
		LongValidator val = LongValidator.getInstance();
		if(val.isValid(sUserId)) {
			userId = Long.parseLong(sUserId);
		}
		
		if(userId == 0L) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownUserError")
				.send(resp);
			return;
		}
		
		UserService userv = ctx.getServiceManager().getService(UserService.class);
		User user = userv.getOne(userId);
		
		if(user == null) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownUserError")
				.send(resp);
			return;
		}
		
		if(!auth.getUser().canEdit(user) && auth.getUser().getId() != user.getId()) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "insufficientPermissionError")
				.send(resp);
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
		} else {
			user.setEmail(email);
		}
		
		if(!username.matches("[A-Za-zäöüÄÖÜß_-]{1,50}")) {
			fail = true;
			errors.add("usernameInvalidError");
		} else {
			user.setUsername(username);
		}
		
		if(password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&#])[A-Za-z0-9@$!%*?&#]{8,100}$")) {
			user.setPassword(password);
		} else if(!password.isBlank()) {
			fail = true;
			errors.add("passwordInvalidError");
		}
		
		if(fail) {
			ctx.getTemplate("admin/userEdit.tpl").assign("failed", true).assign("errors", errors).send(resp);
			return;
		}
		
		User otherUser;
		
		if((otherUser = userv.getByUsername(username)) != null && otherUser.getId() != user.getId()) {
			errors.add("usernameTakenError");
			ctx.getTemplate("admin/userEdit.tpl").assign("failed", true).assign("errors", errors).send(resp);
			return;
		} 
		
		otherUser = null;
		
		if((otherUser = userv.getByEmail(email)) != null && otherUser.getId() != user.getId()) {
			errors.add("emailTakenError");
			ctx.getTemplate("admin/userEdit.tpl").assign("failed", true).assign("errors", errors).send(resp);
			return;
		}
		
		GroupService gserv = ctx.getServiceManager().getService(GroupService.class);
		List<Group> groups = gserv.getAll();
		
		user.getGroups().clear();
		
		for(Group group : groups) {
			LogManager.getLogger().debug("Edit:Checking group: " + group.getId());
			String groupParam = req.getParameter("groupMembership-" + group.getId());
			
			if(groupParam == null || groupParam.equalsIgnoreCase("false")) {
				continue;
			}
			
			if(!auth.getUser().canEdit(group)) {
				LogManager.getLogger().debug("Edit:Not editable: " + group.getId());
				continue;
			}
			
			LogManager.getLogger().debug("Edit:Adding group: " + group);
			user.addGroup(group);
		}
		
		userv.save(user);
		resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/user/edit/" + user.getId() + "?saved"));
	}
}
