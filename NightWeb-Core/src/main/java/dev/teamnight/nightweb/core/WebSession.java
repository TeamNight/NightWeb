/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.logging.log4j.LogManager;

import dev.teamnight.nightweb.core.entities.Permission;
import dev.teamnight.nightweb.core.entities.User;

/**
 * @author Jonas
 *
 */
public class WebSession implements HttpSessionBindingListener {

	//TODO implement update process during handling
	
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
		
		if(expectedType.isAssignableFrom(sessionObj.getClass())) {
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
	
	/**
	 * Removes variables associated to a logged-in user, override this for your specific session and call super.flush();
	 */
	public void flush() {
		this.user = null;
	}

	/**
	 * @param ctx 
	 * This method retrieves the data for all variables from the db and attaches them with the current session of Hibernate
	 * Overwrite this method if you have own variables or if you are using a subclass of user
	 */
	public void update() {
		if(this.user != null) {
			this.user = this.context.getDatabaseSession().get(User.class, this.user.getId());
		}
	}
	
}
