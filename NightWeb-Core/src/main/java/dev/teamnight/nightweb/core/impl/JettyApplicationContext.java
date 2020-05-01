/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.lang.annotation.Annotation;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Authenticated;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.service.ServiceManager;

public class JettyApplicationContext implements ApplicationContext {

	private static FilterHolder authenticationFilter = new FilterHolder(new AuthenticationFilter());
	
	private final ServletContextHandler handler;
	private final SessionFactory factory;
	private final ServiceManager serviceManager;
	private Application application;
	
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
		for(Annotation annotation : servlet.getAnnotations()) {
			if(annotation instanceof Authenticated) {
				this.handler.addFilter(authenticationFilter, pathSpec, EnumSet.allOf(DispatcherType.class));
			}
		}
		
		this.handler.addServlet(servlet, pathSpec);
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet) throws IllegalArgumentException {
		WebServlet webServlet = servlet.getAnnotation(WebServlet.class);
		
		if(webServlet == null) {
			throw new IllegalArgumentException("WebServlet annotation is missing on " + servlet.getCanonicalName());
		}
		
		Authenticated auth = servlet.getAnnotation(Authenticated.class);
		if(auth != null) {
			for(String url : webServlet.urlPatterns()) {
				this.handler.addFilter(authenticationFilter, url, EnumSet.allOf(DispatcherType.class));
			}
			
		}
		
		for(String url : webServlet.urlPatterns()) {
			this.handler.addServlet(servlet, url);
		}
	}

	@Override
	public Session getDatabaseSession() {
		return this.factory.openSession();
	}

	@Override
	public Logger getLogger() {
		return LogManager.getLogger(this.application.getClass());
	}

	@Override
	public Logger getLogger(Class<?> object) {
		return LogManager.getLogger(object);
	}

	@Override
	public String getContextPath() {
		return this.handler.getContextPath();
	}

	@Override
	public SessionFactory getSessionFactory() {
		return this.factory;
	}

	@Override
	public void setModule(NightModule module) {
		if(module instanceof Application) {
			this.application = (Application)module;
		} else {
			throw new IllegalArgumentException("module has to be an Application");
		}
	}

	@Override
	public ServiceManager getServiceManager() {
		return this.serviceManager;
	}

}
