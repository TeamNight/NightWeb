/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.Router;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.template.TemplateBuilder;
import dev.teamnight.nightweb.core.template.TemplateManager;
import dev.teamnight.nightweb.core.util.ServletBuilder;

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
	public String getModuleIdentifier() {
		return this.module.getIdentifier();
	}
	
	@Override
	public void addServlet(Class<? extends HttpServlet> servlet) {
		appContext.addServlet(servlet);
	}
	
	@Override
	public void addServlet(Class<? extends HttpServlet> servlet, String pathSpec) {
		appContext.addServlet(servlet, pathSpec);
	}
	
	@Override
	public void addServlet(ServletBuilder builder, String pathSpec) {
		appContext.addServlet(builder, pathSpec);
	}
	
	@Override
	public void addController(Controller controller) {
		this.appContext.addController(controller);
	}
	
	@Override
	public Router getRouter() {
		return this.appContext.getRouter();
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
	public TemplateManager getTemplateManager() {
		return this.appContext.getTemplateManager();
	}
	
	@Override
	public Authenticator getAuthenticator(HttpSession session) {
		return this.appContext.getAuthenticator(session);
	}

	@Override
	public TemplateBuilder getTemplate(String templatePath) {
		return this.getTemplateManager().builder(templatePath, this);
	}

	@Override
	public Context getParent() {
		return this.appContext;
	}

}
