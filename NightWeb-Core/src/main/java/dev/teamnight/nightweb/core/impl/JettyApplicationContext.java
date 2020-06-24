/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.lang.annotation.Annotation;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.AuthenticatorFactory;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.annotations.Authenticated;
import dev.teamnight.nightweb.core.annotations.IgnoreForceLogin;
import dev.teamnight.nightweb.core.entities.ApplicationData;
import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.FilterEntry;
import dev.teamnight.nightweb.core.mvc.Router;
import dev.teamnight.nightweb.core.mvc.SecurityFilter;
import dev.teamnight.nightweb.core.mvc.ServletRouterImpl;
import dev.teamnight.nightweb.core.service.ApplicationService;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.template.TemplateBuilder;
import dev.teamnight.nightweb.core.template.TemplateManager;
import dev.teamnight.nightweb.core.util.StringUtil;

public class JettyApplicationContext implements ApplicationContext {

	private ApplicationData coreData;
	private FilterHolder adminAuthenticationFilter;
	private FilterHolder authenticationFilter;
	private FilterHolder authorizationFilter;
	private FilterHolder requestFilter;
	
	private final ServletContextHandler handler;
	private final SessionFactory factory;
	private final AuthenticatorFactory authFactory;
	private final ServiceManager serviceManager;
	private final TemplateManager templateManager; //TODO maybe change this two to NightWebCore in order to be available all the time
	private Application application;
	private ServletRouterImpl router;
	
	/**
	 * @param handler
	 * @param factory
	 * @param serviceManager
	 */
	public JettyApplicationContext(ServletContextHandler handler, SessionFactory factory, AuthenticatorFactory auth, ServiceManager serviceManager, TemplateManager templateManager) {
		this.handler = handler;
		this.factory = factory;
		this.authFactory = auth;
		this.serviceManager = serviceManager;
		this.templateManager = templateManager;
		
		this.coreData = this.getServiceManager().getService(ApplicationService.class).getByIdentifier("dev.teamnight.nightweb.core");
		this.adminAuthenticationFilter = new FilterHolder(new AdminAuthenticationFilter(this, this.coreData));
		this.authenticationFilter = new FilterHolder(new AuthenticationFilter(this, this.coreData));
		this.authorizationFilter = new FilterHolder(new AuthorizationFilter(this));
		this.requestFilter = new FilterHolder(new RequestFilter(this, this.coreData));
		
		SecurityFilter authFilter = (SecurityFilter) this.authenticationFilter.getFilter();
		SecurityFilter adminFilter = (SecurityFilter) this.authorizationFilter.getFilter();
		
		this.router = new ServletRouterImpl(this, authFilter, adminFilter);
		this.handler.addServlet(new ServletHolder(this.router), "/*");
		
		this.handler.addFilter(this.requestFilter, "/*", EnumSet.allOf(DispatcherType.class));
		this.handler.addFilter(this.authenticationFilter, "/*", EnumSet.allOf(DispatcherType.class));
		this.handler.addFilter(this.authorizationFilter, "/*", EnumSet.allOf(DispatcherType.class));
	}
	
	@Override
	public String getModuleIdentifier() {
		return this.application.getIdentifier();
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathSpec, Context ctx) {
		WebServlet webServlet = null;
		
		if(pathSpec == null) {
			webServlet = servlet.getAnnotation(WebServlet.class);
			
			if(webServlet == null) {
				throw new IllegalArgumentException("WebServlet annotation is missing on " + servlet.getCanonicalName());
			}
		}
		
		SecurityFilter filter = (SecurityFilter) this.authenticationFilter.getFilter();
		
		Authenticated auth = servlet.getAnnotation(Authenticated.class);
		if(auth != null) {
			if(webServlet != null) {
				for(String url : webServlet.urlPatterns()) {
					LogManager.getLogger().info("Adding auth to " + url + "." + servlet.getCanonicalName()  + ": " + (servlet.getAnnotation(Authenticated.class) != null));
					String realUrl = StringUtil.filterURL(this.getContextPath() + url).replace("*", "(.*)");
					
					FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
					
					filter.addPattern(entry);
				}
			} else {
				String realUrl = StringUtil.filterURL(this.getContextPath() + pathSpec).replace("*", "(.*)");
				FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
				
				filter.addPattern(entry);
			}
		}
		
		AdminServlet admin = servlet.getAnnotation(AdminServlet.class);
		if(admin != null) {
			if(webServlet != null) {
				for(String url : webServlet.urlPatterns()) {
					String realUrl = StringUtil.filterURL(this.getContextPath() + url).replace("*", "(.*)");
					FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
					
					filter.addPattern(entry);
				}
			} else {
				String realUrl = StringUtil.filterURL(this.getContextPath() + pathSpec).replace("*", "(.*)");
				FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
				
				filter.addPattern(entry);
			}
		}
		
		this.registerServlet(new ServletHolder(servlet), pathSpec);
	}
	
	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathSpec) {
		for(Annotation annotation : servlet.getAnnotations()) {
			if(annotation instanceof Authenticated) {
				LogManager.getLogger().info("Adding auth to " + pathSpec + "." + servlet.getCanonicalName()  + ": " + (servlet.getAnnotation(Authenticated.class) != null));
				
				SecurityFilter filter = (SecurityFilter) this.authenticationFilter.getFilter();
				
				String realUrl = StringUtil.filterURL(this.getContextPath() + pathSpec).replace("*", "(.*)");
				FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
				
				filter.addPattern(entry);
			} else if(annotation instanceof AdminServlet) {
				this.handler.addFilter(adminAuthenticationFilter, pathSpec, EnumSet.allOf(DispatcherType.class));
			} else if(annotation instanceof IgnoreForceLogin) {
				RequestFilter filter = (RequestFilter) this.requestFilter.getFilter();
				filter.addIgnoreForceLoginPath(pathSpec);
			}
		}
		
		this.registerServlet(new ServletHolder(servlet), pathSpec);
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet) throws IllegalArgumentException {
		WebServlet webServlet = servlet.getAnnotation(WebServlet.class);
		
		if(webServlet == null) {
			throw new IllegalArgumentException("WebServlet annotation is missing on " + servlet.getCanonicalName());
		}
		
		SecurityFilter filter = (SecurityFilter) this.authenticationFilter.getFilter();
		
		Authenticated auth = servlet.getAnnotation(Authenticated.class);
		if(auth != null) {
			for(String url : webServlet.urlPatterns()) {
				LogManager.getLogger().info("Adding auth to " + url + "." + servlet.getCanonicalName()  + ": " + (servlet.getAnnotation(Authenticated.class) != null));
				
				
				String realUrl = StringUtil.filterURL(this.getContextPath() + url).replace("*", "(.*)");
				FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
				
				filter.addPattern(entry);
			}
			
		}
		
		AdminServlet admin = servlet.getAnnotation(AdminServlet.class);
		if(admin != null) {
			for(String url : webServlet.urlPatterns()) {
				String realUrl = StringUtil.filterURL(this.getContextPath() + url).replace("*", "(.*)");
				FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
				
				filter.addPattern(entry);
			}
		}
		
		for(String url : webServlet.urlPatterns()) {
			this.registerServlet(new ServletHolder(servlet), url);
		}
	}

	@Override
	public void registerServletHolder(ServletHolder holder, String pathSpec) {
		Authenticated auth = holder.getHeldClass().getAnnotation(Authenticated.class);
		if(auth != null) {
			LogManager.getLogger().info("Adding auth to " + pathSpec + "." + holder.getHeldClass().getCanonicalName()  + ": " + (holder.getHeldClass().getAnnotation(Authenticated.class) != null));
			SecurityFilter filter = (SecurityFilter) this.authenticationFilter.getFilter();
			
			String realUrl = StringUtil.filterURL(this.getContextPath() + pathSpec).replace("*", "(.*)");
			FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
			
			filter.addPattern(entry);
		}
		AdminServlet admin = holder.getClass().getAnnotation(AdminServlet.class);
		if(admin != null) {
			this.handler.addFilter(adminAuthenticationFilter, pathSpec, EnumSet.allOf(DispatcherType.class));
		}
		
		this.registerServlet(holder, pathSpec);
	}

	private void registerServlet(ServletHolder holder, String pathSpec) {
		holder.setName(holder.getHeldClass().getName());
		this.handler.addServlet(holder, pathSpec);
	}
	
	@Override
	public void addController(Controller controller) {
		this.getRouter().addController(controller);
	}
	
	@Override
	public Router getRouter() {
		return this.router;
	}

	@Override
	public Session getDatabaseSession() {
		return this.factory.getCurrentSession();
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

	@Override
	public TemplateManager getTemplateManager() {
		return this.templateManager;
	}
	
	@Override
	public TemplateBuilder getTemplate(String templatePath) {
		return this.templateManager.builder(templatePath, this);
	}
	
	@Override
	public Authenticator getAuthenticator(HttpSession session) {
		return this.authFactory.getAuthenticator(this, session);
	}

	@Override
	public Context getParent() {
		return this;
	}

}
