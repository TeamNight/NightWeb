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
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.Group;
import dev.teamnight.nightweb.core.service.GroupService;
import dev.teamnight.nightweb.core.template.Pagination;
import dev.teamnight.nightweb.core.util.PathParameters;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminGroupListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canEditGroups")) {
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
		
		GroupService gserv = ctx.getServiceManager().getService(GroupService.class);
		
		long groupCount = gserv.count();
		
		Pagination pagination = new Pagination(groupCount, limit).currentPage(page+1);
		
		List<Group> groups = gserv.getMultiple(offset, limit);
		groups.forEach(group -> group.setMemberSize(gserv.getUserCount(group)));
		
		ctx.getTemplate("admin/groupList.tpl")
			.assign("currentUser", auth.getUser())
			.assign("groups", groups)
			.assign("pagination", pagination)
			.send(resp);
	}
	
}
