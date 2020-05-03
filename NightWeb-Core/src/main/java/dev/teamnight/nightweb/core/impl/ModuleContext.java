/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Authenticated;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.WebSession;
import dev.teamnight.nightweb.core.service.ServiceManager;

public class ModuleContext implements Context {

	private ApplicationContext appContext;
	private NightModule module;
	private Class<? extends WebSession> sessionType;
	
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
	public void registerServlet(Class<? extends HttpServlet> servlet) {
		WebServlet webServlet = servlet.getAnnotation(WebServlet.class);
		
		if(webServlet == null) {
			throw new IllegalArgumentException("WebServlet annotation is missing on " + servlet.getCanonicalName());
		}
		
		this.appContext.registerServlet(servlet, null, this);
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathInfo) {
		this.appContext.registerServlet(servlet, pathInfo, this);
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

	@Override
	public Class<? extends WebSession> getSessionType() {
		return this.sessionType;
	}
	
	@Override
	public void setSessionType(Class<? extends WebSession> sessionType) throws IllegalArgumentException {
		if(this.sessionType != null) {
			throw new IllegalArgumentException("Session Type can only be set once");
		}
		
		try {
			this.sessionType.getConstructor(Context.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Missing public " + this.sessionType.getSimpleName() + "(Context) Constructor", e);
		}
		
		this.sessionType = sessionType;
	}

}
