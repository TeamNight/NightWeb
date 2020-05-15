/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.teamnight.nightweb.core.AdminSession;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.PathParameters;
import dev.teamnight.nightweb.core.WebSession;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.ApplicationData;
import dev.teamnight.nightweb.core.entities.ModuleData;
import dev.teamnight.nightweb.core.service.ApplicationService;
import dev.teamnight.nightweb.core.service.ModuleService;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminModuleEditServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		AdminSession session = WebSession.getSession(req, AdminSession.class);
		
		ModuleService mserv = ctx.getServiceManager().getService(ModuleService.class);
		
		String moduleIdentifier = new PathParameters(req.getPathInfo()).getParameter(0);
		
		if(moduleIdentifier == null || moduleIdentifier.isBlank()) {
			ctx.getTemplate("admin/moduleEdit.tpl")
				.assign("session", session)
				.assign("failed", "unknownModuleError")
				.send(resp);
			return;
		}
		
		ModuleData data = mserv.getByIdentifier(moduleIdentifier);
		
		if(data == null) {
			ctx.getTemplate("admin/moduleEdit.tpl")
				.assign("session", session)
				.assign("failed", "unknownModuleError")
				.send(resp);
			return;
		}
		
		ctx.getTemplate("admin/moduleEdit.tpl")
			.assign("session", session)
			.assign("module", data)
			.assign("appData", ctx.getServiceManager().getService(ApplicationService.class).getByIdentifier(moduleIdentifier))
			.send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		AdminSession session = WebSession.getSession(req, AdminSession.class);
		
		ModuleService mserv = ctx.getServiceManager().getService(ModuleService.class);
		
		String moduleIdentifier = new PathParameters(req.getPathInfo()).getParameter(0);
		
		if(moduleIdentifier == null || moduleIdentifier.isBlank()) {
			ctx.getTemplate("admin/moduleEdit.tpl")
				.assign("session", session)
				.assign("failed", "unknownModuleError")
				.send(resp);
			return;
		}
		
		ModuleData data = mserv.getByIdentifier(moduleIdentifier);
		
		if(data == null) {
			ctx.getTemplate("admin/moduleEdit.tpl")
				.assign("session", session)
				.assign("failed", "unknownModuleError")
				.send(resp);
			return;
		}
		
		String path = req.getParameter("contextPath");
		String isEnabled = req.getParameter("enabled");
		
		ApplicationData appData = ctx.getServiceManager().getService(ApplicationService.class).getByIdentifier(moduleIdentifier);
		
		if(path != null && appData != null) {
			if(!path.matches("^^[a-zA-Z0-9/-_.+!]+?\\/+$")) {
				ctx.getTemplate("admin/moduleEdit.tpl")
					.assign("session", session)
					.assign("module", data)
					.assign("appData", appData)
					.assign("pathError", "unallowedPathError")
					.send(resp);
				return;
			}
			
			appData.setContextPath(path);
			ctx.getServiceManager().getService(ApplicationService.class).save(appData);
		}
		
		if(isEnabled != null) {
			data.setEnabled(true);
		} else {
			data.setEnabled(false);
		}
		
		mserv.save(data);
		
		ctx.getTemplate("admin/moduleEdit.tpl")
			.assign("session", session)
			.assign("module", mserv.getByIdentifier(moduleIdentifier))
			.assign("appData", appData)
			.assign("saved", true)
			.send(resp);
	}
	
}
