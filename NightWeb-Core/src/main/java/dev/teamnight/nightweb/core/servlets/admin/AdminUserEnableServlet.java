/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.LongValidator;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.PathParameters;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminUserEnableServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canDisableUsers")) {
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
			resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/users?error=unknownUser"));
			return;
		}
		
		UserService userv = ctx.getServiceManager().getService(UserService.class);
		User user = userv.getOne(userId);
		
		if(user == null) {
			resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/users?error=unknownUser"));
			return;
		}
		
		if(!auth.getUser().canEdit(user) || auth.getUser().getId() == user.getId()) {
			resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/users?error=insufficientPermission"));
			return;
		}
		
		user.setDisabled(false);
		
		userv.save(user);
		resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/users?enabled"));
	}
	
}
