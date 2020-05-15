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

import org.apache.commons.validator.routines.IntegerValidator;

import dev.teamnight.nightweb.core.AdminSession;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.NightWebCore;
import dev.teamnight.nightweb.core.PathParameters;
import dev.teamnight.nightweb.core.WebSession;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.ModuleData;
import dev.teamnight.nightweb.core.impl.NightWebCoreImpl;
import dev.teamnight.nightweb.core.module.ModuleManager;
import dev.teamnight.nightweb.core.service.ModuleService;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminModuleListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		AdminSession session = WebSession.getSession(req, AdminSession.class);
		
		ModuleService mserv = ctx.getServiceManager().getService(ModuleService.class);
		
		String sPage = new PathParameters(req.getPathInfo()).getParameter(0);
		
		int page = 0;
		
		IntegerValidator val = IntegerValidator.getInstance();
		if(val.isValid(sPage)) {
			page = Integer.parseInt(sPage) - 1;
		}
		
		int limit = 30;
		int offset = (page != 0 ? page * limit : 0);
		
		List<ModuleData> modules = mserv.getMultiple(offset, limit);
		
		ModuleManager moduleMan = this.getModuleManager();
		List<NightModule> uninstalledModules = null;
		
		if(moduleMan != null) {
			uninstalledModules = moduleMan.getLoadedModules().stream().filter(module -> !moduleMan.getEnabledModules().contains(module)).collect(Collectors.toList());
		}
		
		ctx.getTemplate("admin/moduleList.tpl")
			.assign("session", session)
			.assign("modules", modules)
			.assign("uninstalledModules", uninstalledModules)
			.send(resp);
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
