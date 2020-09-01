/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.Objects;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.AuthenticatorFactory;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.ServletRegistrationAdapter;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.annotations.Authenticated;
import dev.teamnight.nightweb.core.annotations.IgnoreForceLogin;
import dev.teamnight.nightweb.core.entities.ApplicationData;
import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.FilterEntry;
import dev.teamnight.nightweb.core.mvc.Router;
import dev.teamnight.nightweb.core.mvc.SecurityFilter;
import dev.teamnight.nightweb.core.service.ApplicationService;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.template.TemplateBuilder;
import dev.teamnight.nightweb.core.template.TemplateManager;
import dev.teamnight.nightweb.core.util.ServletBuilder;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
public class ApplicationContextImpl implements ApplicationContext {

	private ApplicationData coreData;
	private Filter adminAuthenticationFilter;
	private Filter authenticationFilter;
	private Filter authorizationFilter;
	private Filter requestFilter;
	
	private ServletRegistrationAdapter adapter;
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
	public ApplicationContextImpl(SessionFactory factory, AuthenticatorFactory auth, ServiceManager serviceManager, TemplateManager templateManager) {
		this.factory = factory;
		this.authFactory = auth;
		this.serviceManager = serviceManager;
		this.templateManager = templateManager;
		
		this.coreData = this.getServiceManager().getService(ApplicationService.class).getByIdentifier("dev.teamnight.nightweb.core");
		this.adminAuthenticationFilter = new AdminAuthenticationFilter(this, this.coreData);
		this.authenticationFilter = new AuthenticationFilter(this, this.coreData);
		this.authorizationFilter = new AuthorizationFilter(this);
		this.requestFilter = new RequestFilter(this, this.coreData);
		
		SecurityFilter authFilter = (SecurityFilter) this.authenticationFilter;
		SecurityFilter adminFilter = (SecurityFilter) this.authorizationFilter;
		
		this.router = new ServletRouterImpl(this, authFilter, adminFilter);
	}
	
	@Override
	public String getModuleIdentifier() {
		return this.application.getIdentifier();
	}

	@Override
	public Context getParent() {
		return this;
	}
	
	@Override
	public ServletRegistrationAdapter getServletRegistrationAdapter() {
		return this.adapter;
	}
	
	@Override
	public void addServlet(Class<? extends HttpServlet> servlet) {
		WebServlet webServlet = servlet.getAnnotation(WebServlet.class);
		
		if(webServlet == null) {
			throw new IllegalArgumentException("WebServlet annotation is missing on " + servlet.getCanonicalName());
		}
		
		SecurityFilter filter = (SecurityFilter) this.authenticationFilter;
		
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
			this.adapter.addServlet(servlet, url);
		}
	}
	
	@Override
	public void addServlet(Class<? extends HttpServlet> servlet, String pathSpec) {
		for(Annotation annotation : servlet.getAnnotations()) {
			if(annotation instanceof Authenticated) {
				LogManager.getLogger().info("Adding auth to " + pathSpec + "." + servlet.getCanonicalName()  + ": " + (servlet.getAnnotation(Authenticated.class) != null));
				
				SecurityFilter filter = (SecurityFilter) this.authenticationFilter;
				
				String realUrl = StringUtil.filterURL(this.getContextPath() + pathSpec).replace("*", "(.*)");
				FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
				
				filter.addPattern(entry);
			} else if(annotation instanceof AdminServlet) {
				this.adapter.addFilter(adminAuthenticationFilter, pathSpec, EnumSet.allOf(DispatcherType.class));
			} else if(annotation instanceof IgnoreForceLogin) {
				RequestFilter filter = (RequestFilter) this.requestFilter;
				filter.addIgnoreForceLoginPath(pathSpec);
			}
		}
		
		this.adapter.addServlet(servlet, pathSpec);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addServlet(ServletBuilder builder, String pathSpec) {
		SecurityFilter filter = (SecurityFilter) this.authenticationFilter;
		
		Class<? extends Servlet> servlet = null;
		
		if(builder.getClassName().isPresent()) {
			Class<?> clazz;
			try {
				clazz = Class.forName(builder.getClassName().get());
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("The class " + builder.getClassName().get() + " was not found", e);
			}
			
			if(!Servlet.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("The class " + builder.getClassName().get() + " does not implement javax.servlet.Servlet");
			}
			
			servlet = (Class<? extends Servlet>) clazz;
		} else if(builder.getHeldClass().isPresent()) {
			servlet = builder.getHeldClass().get();
		} else {
			servlet = builder.getServlet().get().getClass();
		}
		
		Authenticated auth = servlet.getAnnotation(Authenticated.class);
		if(auth != null) {
			LogManager.getLogger().info("Adding auth to " + pathSpec + "." + servlet.getCanonicalName()  + ": " + (servlet.getAnnotation(Authenticated.class) != null));
			
			
			String realUrl = StringUtil.filterURL(this.getContextPath() + pathSpec).replace("*", "(.*)");
			FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
			
			filter.addPattern(entry);
		}
		
		AdminServlet admin = servlet.getAnnotation(AdminServlet.class);
		if(admin != null) {
			String realUrl = StringUtil.filterURL(this.getContextPath() + pathSpec).replace("*", "(.*)");
			FilterEntry entry = new FilterEntry(realUrl, new String[] {"GET", "POST", "PUT", "DELETE"});
			
			filter.addPattern(entry);
		}
		
		this.adapter.addServlet(builder, pathSpec);
	}
	
	@Override
	public void addServlet(ServletBuilder builder, String pathSpec, Context ctx) {
		this.addServlet(builder, pathSpec);
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
		return this.adapter.getContextPath();
	}

	@Override
	public Authenticator getAuthenticator(HttpSession session) {
		return this.authFactory.getAuthenticator(this, session);
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
	public void setServletRegistration(ServletRegistrationAdapter adapter) {
		Objects.requireNonNull(adapter, "Adapter can not be null");
		
		if(this.adapter != null) {
			throw new IllegalStateException("Adapter is already set");
		}
		
		this.adapter = adapter;
		
		//Init the AppContext
		this.adapter.addServlet(new ServletBuilder(this.router).servletName("Router"), "/*");
		
		this.adapter.addFilter(this.requestFilter, "/*", EnumSet.allOf(DispatcherType.class));
		this.adapter.addFilter(this.authenticationFilter, "/*", EnumSet.allOf(DispatcherType.class));
		this.adapter.addFilter(this.authorizationFilter, "/*", EnumSet.allOf(DispatcherType.class));
	}

}
