/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.service.ServiceManager;

public class ModuleContext implements Context {

	private ApplicationContext appContext;
	private NightModule module;
	
	/**
	 * @param appContext
	 * @param module
	 */
	public ModuleContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	protected ApplicationContext getApplicationContext() {
		return this.appContext;
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
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathInfo) {
		//TODO Implement using module param
	}

	@Override
	public Session getDatabaseSession() {
		return this.appContext.getDatabaseSession();
	}

	@Override
	public Logger getLogger() {
		return LogManager.getLogger();
	}

	@Override
	public Logger getLogger(Class<?> object) {
		return LogManager.getLogger(object);
	}

	@Override
	public String getContextPath() {
		return this.appContext.getContextPath();
	}

	@Override
	public void setModule(NightModule module) {
		this.module = module;
	}

	@Override
	public ServiceManager getServiceManager() {
		return this.appContext.getServiceManager();
	}

}
