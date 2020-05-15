/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.teamnight.nightweb.core.AdminSession;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.NightWebCore;
import dev.teamnight.nightweb.core.StringUtil;
import dev.teamnight.nightweb.core.WebSession;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.ModuleMetaFile;
import dev.teamnight.nightweb.core.impl.NightWebCoreImpl;
import dev.teamnight.nightweb.core.module.ModuleManager;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminModuleInstallListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		AdminSession session = WebSession.getSession(req, AdminSession.class);
		
		ModuleManager moduleMan = this.getModuleManager();
		List<NightModule> uninstalledModules = null;
		
		if(moduleMan != null) {
			uninstalledModules = moduleMan.getLoadedModules().stream().filter(module -> !moduleMan.getEnabledModules().contains(module)).collect(Collectors.toList());
		}
		
		List<ModuleMetaFile> uninstalledMetaFiles = uninstalledModules.stream().map(module -> moduleMan.getMeta(module.getIdentifier())).collect(Collectors.toList());
		
		ctx.getTemplate("admin/moduleInstallList.tpl")
			.assign("session", session)
			.assign("uninstalledModules", uninstalledMetaFiles)
			.send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		AdminSession session = WebSession.getSession(req, AdminSession.class);
		
		ModuleManager moduleMan = this.getModuleManager();
		List<NightModule> uninstalledModules = null;
		
		if(moduleMan != null) {
			uninstalledModules = moduleMan.getLoadedModules().stream().filter(module -> !moduleMan.getEnabledModules().contains(module)).collect(Collectors.toList());
		}
		
		List<ModuleMetaFile> uninstalledMetaFiles = uninstalledModules.stream().map(module -> moduleMan.getMeta(module.getIdentifier())).collect(Collectors.toList());
		
		String[] installParam = req.getParameterValues("install");
		
		for(String param : installParam) {
			if(!param.matches("^([a-zA-Z0-9_$]+\\.{0,1})+[a-zA-Z0-9_$]+$")) {
				ctx.getTemplate("admin/moduleInstallList.tpl")
					.assign("session", session)
					.assign("uninstalledModules", uninstalledMetaFiles)
					.assign("failed", "unallowedParamError")
					.send(resp);
			}
		}
		
		resp.sendRedirect(StringUtil.filterURL(ctx.getContextPath() + "/admin" + "/install?modules=" + String.join(",", installParam)));
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
