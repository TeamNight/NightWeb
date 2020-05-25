/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.IntegerValidator;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.PathParameters;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.template.AlertMessage;
import dev.teamnight.nightweb.core.template.Pagination;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminUserListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canManageUsers")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		String sPage = new PathParameters(req.getPathInfo()).getParameter(0);
		
		int page = 0;
		
		IntegerValidator val = IntegerValidator.getInstance();
		if(val.isValid(sPage)) {
			page = Integer.parseInt(sPage) - 1;
		}
		
		int limit = 30;
		int offset = (page != 0 ? page * limit : 0);
		
		UserService userv = ctx.getServiceManager().getService(UserService.class);
		
		long userCount = userv.count();
		Pagination pagination = new Pagination(userCount, limit).currentPage(page+1);
		
		List<User> users = userv.getMultiple(offset, limit);
		users.forEach(user -> userv.loadGroups(user));
		
		AlertMessage msg = null;
		if(req.getParameter("error") != null) {
			msg = new AlertMessage("error." + req.getParameter("error"), AlertMessage.Type.ERROR);
		} else if(req.getParameter("deleted") != null) {
			msg = new AlertMessage("deleted", AlertMessage.Type.SUCCESS);
		} else if(req.getParameter("banned") != null) {
			msg = new AlertMessage("banned", AlertMessage.Type.SUCCESS);
		} else if(req.getParameter("unbanned") != null) {
			msg = new AlertMessage("unbanned", AlertMessage.Type.SUCCESS);
		} else if(req.getParameter("disabled") != null) {
			msg = new AlertMessage("disabled", AlertMessage.Type.SUCCESS);
		} else if(req.getParameter("enabled") != null) {
			msg = new AlertMessage("enabled", AlertMessage.Type.SUCCESS);
		}
		
		//Build the template
		ctx.getTemplate("admin/userList.tpl")
			.assign("currentUser", auth.getUser())
			.assign("users", users)
			.assign("pagination", pagination)
			.assign("msg", msg)
			.send(resp);
	}

}
