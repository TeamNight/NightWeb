/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import dev.teamnight.nightweb.core.service.ServiceManager;

public interface Context {
	
	public void registerServlet(Class<? extends HttpServlet> servlet);
	
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathInfo);
	
	public ServiceManager getServiceManager();
	
	public Session getDatabaseSession();
	
	public Logger getLogger();
	
	public Logger getLogger(Class<?> object);
	
	public String getContextPath();
	
	public void setModule(NightModule module);
	
}
