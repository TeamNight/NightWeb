/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.service.ServiceManager;

public class JettyApplicationContext implements ApplicationContext {

	private final ServletContextHandler handler;
	private final SessionFactory factory;
	private final ServiceManager serviceManager;
	
	/**
	 * @param handler
	 * @param factory
	 * @param serviceManager
	 */
	public JettyApplicationContext(ServletContextHandler handler, SessionFactory factory, ServiceManager serviceManager) {
		this.handler = handler;
		this.factory = factory;
		this.serviceManager = serviceManager;
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathSpec) {
		this.handler.addServlet(servlet, pathSpec);
	}

	@Override
	public void registerServlet(HttpServlet servlet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerServlet(HttpServlet servlet, String pathInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Session getDatabaseSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger(Class<?> object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SessionFactory getSessionFactory() {
		return this.factory;
	}

	@Override
	public void setModule(NightModule module) {
		
	}

	@Override
	public ServiceManager getServiceManager() {
		// TODO Auto-generated method stub
		return null;
	}

}
