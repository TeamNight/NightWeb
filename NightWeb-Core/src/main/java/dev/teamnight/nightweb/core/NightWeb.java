/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import dev.teamnight.nightweb.core.service.GroupService;
import dev.teamnight.nightweb.core.service.PermissionService;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.service.UserService;

public final class NightWeb {

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

	public static String getTemplateManager() {
		return core.getTemplateManager();
	}

	public static String getMailService() {
		return core.getMailService();
	}

	public static UserService getUserService() {
		return core.getUserService();
	}

	public static GroupService getGroupService() {
		// TODO Auto-generated method stub
		return core.getGroupService();
	}

	public static PermissionService getPermissionService() {
		// TODO Auto-generated method stub
		return core.getPermissionService();
	}
	
}
