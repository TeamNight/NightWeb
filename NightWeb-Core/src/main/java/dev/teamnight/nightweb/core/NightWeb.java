/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import dev.teamnight.nightweb.core.entities.ErrorLogEntry;
import dev.teamnight.nightweb.core.service.ErrorLogService;
import dev.teamnight.nightweb.core.service.GroupService;
import dev.teamnight.nightweb.core.service.PermissionService;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.template.TemplateManager;

public final class NightWeb {

	public static final Path WORKING_DIR = Paths.get(System.getProperty("user.dir"));
	public static final Path MODULES_DIR = WORKING_DIR.resolve("modules");
	public static final Path TEMPLATES_DIR = WORKING_DIR.resolve("templates");
	public static final Path STATIC_DIR = WORKING_DIR.resolve("static");
	public static final Path LANG_DIR = WORKING_DIR.resolve("languages");
	
	private static NightWebCore core;
	
	public static NightWebCore getCoreApplication() {
		return NightWeb.core;
	}
	
	public static void setCoreApplication(NightWebCore core) throws IllegalArgumentException{
		if(NightWeb.core != null) {
			throw new IllegalArgumentException("NightWebCore can only be set once");
		}
		
		NightWeb.core = core;
	}

	public static ServiceManager getServiceManager() {
		return core.getServiceManager();
	}

	public static TemplateManager getTemplateManager() {
		return core.getTemplateManager();
	}

	public static String getMailService() {
		return core.getMailService();
	}

	public static UserService getUserService() {
		return core.getUserService();
	}

	public static GroupService getGroupService() {
		return core.getGroupService();
	}

	public static PermissionService getPermissionService() {
		return core.getPermissionService();
	}
	
	public static void logError(Throwable error, Class<?> exceptionLocation) {
		ErrorLogEntry entry = new ErrorLogEntry();
		entry.setClassName(exceptionLocation.getCanonicalName());
		entry.setErrorName(error.getClass().getCanonicalName());
		entry.setErrorMessage(error.getMessage());
		entry.setTime(new Date());
		
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		error.printStackTrace(pw);
		
		entry.setStackTrace(writer.toString());
		
		ErrorLogService service = core.getServiceManager().getService(ErrorLogService.class);
		service.save(entry);
	}
	
}
