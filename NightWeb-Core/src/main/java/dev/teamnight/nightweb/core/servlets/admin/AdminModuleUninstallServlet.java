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
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.ModuleData;
import dev.teamnight.nightweb.core.service.ModuleService;
import dev.teamnight.nightweb.core.util.PathParameters;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminModuleUninstallServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canUninstallModules")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		String sId = new PathParameters(req.getPathInfo()).getParameter(0);
		
		LongValidator val = LongValidator.getInstance();
		if(!val.isValid(sId)) {
			ctx.getTemplate("admin/deleteModule.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "invalidIDError")
				.send(resp);
			return;
		}
		
		long id = Long.parseLong(sId);
		
		ModuleService serv = ctx.getServiceManager().getService(ModuleService.class);
		ModuleData data = serv.getOne(id);
		
		if(data == null) {
			ctx.getTemplate("admin/deleteModule.tpl")
				.assign("session", auth.getUser())
				.assign("failed", "unknownModuleError")
				.send(resp);
			return;
		}
		
		if(data.getIdentifier().equalsIgnoreCase("dev.teamnight.nightweb.core")) {
			ctx.getTemplate("admin/deleteModule.tpl")
				.assign("session", auth.getUser())
				.assign("failed", "unknownModuleError")
				.send(resp);
			return;
		}
		
		serv.delete(data);
		resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/modules?deleted=true"));
	}
	
}
