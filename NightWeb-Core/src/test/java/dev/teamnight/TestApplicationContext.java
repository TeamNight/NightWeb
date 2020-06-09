/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.template.TemplateBuilder;
import dev.teamnight.nightweb.core.template.TemplateManager;

/**
 * @author Jonas
 *
 */
public class TestApplicationContext implements ApplicationContext {
	
	@Override
	public String getModuleIdentifier() {
		return "test";
	}

	@Override
	public Context getParent() {
		return this;
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet) {
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathSpec) {
	}

	@Override
	public ServiceManager getServiceManager() {
		return null;
	}

	@Override
	public TemplateManager getTemplateManager() {
		return null;
	}

	@Override
	public TemplateBuilder getTemplate(String templatePath) {
		return null;
	}

	@Override
	public Session getDatabaseSession() {
		return null;
	}

	@Override
	public Logger getLogger() {
		return LogManager.getLogger();
	}

	@Override
	public Logger getLogger(Class<?> clazz) {
		return LogManager.getLogger(clazz);
	}

	@Override
	public String getContextPath() {
		return "/";
	}

	@Override
	public void setModule(NightModule module) {
	}

	@Override
	public Authenticator getAuthenticator(HttpSession session) {
		return null;
	}

	@Override
	public SessionFactory getSessionFactory() {
		return null;
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathSpec, Context ctx) {
	}

	@Override
	public void registerServletHolder(ServletHolder holder, String pathSpec) {
	}

}
