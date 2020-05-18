/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.NightWebCore;
import dev.teamnight.nightweb.core.StringUtil;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.ApplicationData;
import dev.teamnight.nightweb.core.entities.ModuleData;
import dev.teamnight.nightweb.core.entities.ModuleMetaFile;
import dev.teamnight.nightweb.core.impl.NightWebCoreImpl;
import dev.teamnight.nightweb.core.module.ModuleManager;
import dev.teamnight.nightweb.core.service.ApplicationService;
import dev.teamnight.nightweb.core.service.ModuleService;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminModuleInstallServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canInstallModules")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		ModuleService mserv = ctx.getServiceManager().getService(ModuleService.class);
		
		String param = req.getParameter("modules");
		String[] modules = null;
		
		if(param == null) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unallowedParamError")
				.assign("errorReason", "null")
				.send(resp);
			return;
		}
		
		if(param.contains(",")) {
			modules = param.split(",");
		} else {
			modules = new String[] {param};
		}
		
		String moduleIdentifier = modules[0];
		
		if(moduleIdentifier.isBlank()) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unallowedParamError")
				.assign("errorReason", "blank")
				.send(resp);
			return;
		}
		
		if(mserv.getByIdentifier(moduleIdentifier) != null) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "alreadyInstalledError")
				.send(resp);
			return;
		}
		
		ModuleManager manager = this.getModuleManager();
		NightModule module = manager.getModuleByIdentifier(moduleIdentifier);
		ModuleMetaFile file = manager.getMeta(moduleIdentifier);
		
		if(module == null) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownModuleError")
				.send(resp);
			return;
		}

		manager.installModule(module);
		
		if(module instanceof Application) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("module", file)
				.assign("moduleIdentifiers", param)
				.send(resp);
		} else {
			if(modules.length > 1) {
				String newModules = String.join(",", Arrays.copyOfRange(modules, 1, modules.length));
				resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/install?modules=" + newModules));
			} else {
				resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/modules"));
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canInstallModules")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		ModuleService mserv = ctx.getServiceManager().getService(ModuleService.class);
		ModuleManager manager = this.getModuleManager();
		
		String param = req.getParameter("modules");
		String[] modules = null;
		
		if(param == null) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unallowedParamError")
				.assign("errorReason", "null")
				.send(resp);
			return;
		}
		
		if(param.contains(",")) {
			modules = param.split(",");
		} else {
			modules = new String[] {param};
		}
		
		String moduleIdentifier = modules[0];
		
		if(moduleIdentifier.isBlank()) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unallowedParamError")
				.assign("errorReason", "blank")
				.send(resp);
			return;
		}
		
		ModuleData data = mserv.getByIdentifier(moduleIdentifier);
		ModuleMetaFile file = manager.getMeta(moduleIdentifier);
		
		if(data == null) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownModuleError")
				.send(resp);
			return;
		}
		
		String contextPath = req.getParameter("contextPath");
		
		if(!contextPath.matches("^^[a-zA-Z0-9/-_.+!]+?\\/+$")) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("module", file)
				.assign("moduleIdentifiers", param)
				.assign("failed", "unallowedPathError")
				.send(resp);
			return;
		}
		
		ApplicationService aserv = ctx.getServiceManager().getService(ApplicationService.class);
		
		ApplicationData appData = new ApplicationData();
		appData.setName(data.getName());
		appData.setIdentifier(data.getIdentifier());
		appData.setVersion(data.getVersion());
		appData.setModuleData(data);
		appData.setContextPath(contextPath);
		
		if(aserv.getByIdentifier(moduleIdentifier) != null) {
			ctx.getTemplate("admin/moduleInstall.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "alreadyInstalledError")
				.send(resp);
			return;
		}
		
		aserv.save(appData);
		
		if(modules.length > 1) {
			String newModules = String.join(",", Arrays.copyOfRange(modules, 1, modules.length));
			resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/install?modules=" + newModules));
		} else {
			resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin/modules"));
		}
	}
	
	private ModuleManager getModuleManager() {
		ModuleManager manager = null;
		NightWebCore core = NightWeb.getCoreApplication();
		
		if(core instanceof NightWebCoreImpl) {
			NightWebCoreImpl impl = (NightWebCoreImpl) core;
			manager = impl.getModuleManager();
		} else {
			Field managerField = null;
			
			try {
				managerField = core.getClass().getField("moduleManager");
			} catch (NoSuchFieldException e) {
				try {
					managerField = core.getClass().getDeclaredField("moduleManager");
					managerField.setAccessible(true);
				} catch (NoSuchFieldException e1) {
					e1.printStackTrace();
				} catch (SecurityException e1) {
					e1.printStackTrace();
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			
			try {
				manager = (ModuleManager) managerField.get(core);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return manager;
	}
}
