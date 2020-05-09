/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.logging.log4j.LogManager;

import dev.teamnight.nightweb.core.entities.GroupPermission;
import dev.teamnight.nightweb.core.entities.Permission;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.entities.UserPermission;

/**
 * @author Jonas
 *
 */
public class WebSession implements HttpSessionBindingListener {

	private Context context;
	private User user;
	
	public static WebSession getSession(HttpServletRequest request) {
		Object sessionObj = request.getSession().getAttribute("session");
		
		if(sessionObj == null) {
			return null;
		}
		
		return (WebSession) sessionObj;
	}
	
	public static <T extends WebSession> T getSession(HttpServletRequest request, Class<T> expectedType) {
		Object sessionObj = request.getSession().getAttribute("session");
		
		if(sessionObj == null) {
			return null;
		}
		
		if(sessionObj.getClass().isAssignableFrom(expectedType)) {
			return expectedType.cast(sessionObj);
		}
		return null;
	}
	
	public WebSession(Context ctx) {
		this.context = ctx;
	}
	
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		LogManager.getLogger().info("Null checks: " + String.valueOf(NightWeb.getCoreApplication() != null) + " " + String.valueOf(this != null));
		NightWeb.getCoreApplication().addSession(this);
	}
	
	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		NightWeb.getCoreApplication().removeSession(this);
	}
	
	/**
	 * @return the context
	 */
	protected Context getContext() {
		return context;
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	public boolean isLoggedIn() {
		if(this.user != null) {
			return true;
		}
		return false;
	}

	public boolean hasPermission(Permission permission) {
		if(!this.isLoggedIn()) {
			return false;
		}
		
		return this.hasPermission(permission.getName());
	}

	public boolean hasPermission(String permission) {
		if(!this.isLoggedIn()) {
			return false;
		}
		
		return this.user.hasPermission(permission);
	}
	
}
