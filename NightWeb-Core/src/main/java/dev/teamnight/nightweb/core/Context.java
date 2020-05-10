/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.template.TemplateBuilder;
import dev.teamnight.nightweb.core.template.TemplateManager;

public interface Context {
	
	/**
	 * Registers a servlet in the handler of the ApplicationContext.
	 * Modules do not get an own ContextHandler.
	 * 
	 * <p>Requires the WebServlet annotation with the urlPatterns parameter set
	 * 
	 * @param servlet
	 */
	public void registerServlet(Class<? extends HttpServlet> servlet);
	
	/**
	 * Registers a servlet using a path specification.
	 * 
	 * <p>Do not requires the WebServlet annotation<p>
	 * 
	 * @see Context#registerServlet(Class)
	 * 
	 * @param servlet
	 * @param pathInfo
	 */
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathSpec);
	
	/**
	 * Returns the global Service Manager containing all registered services.
	 * @return {@link dev.teamnight.nightweb.core.services.ServiceManager} the Service Manager
	 */
	public ServiceManager getServiceManager();
	
	/**
	 * Returns the global Template Manager caching all templates
	 * @return {@link dev.teamnight.nightweb.core.template.TemplateManager} the template Manager
	 */
	public TemplateManager getTemplateManager();
	
	/**
	 * Shortcut and helper for TemplateManager#builder(String, Context)
	 */
	public TemplateBuilder getTemplate(String templatePath);
	
	/**
	 * Returns a database session.
	 * @return {@link org.hibernate.Session} a Session
	 */
	public Session getDatabaseSession();
	
	/**
	 * Returns a logger binded to the Context object
	 * @return
	 */
	public Logger getLogger();
	
	/**
	 * Returns a logger binded to the given class
	 * @param object
	 * @return
	 */
	public Logger getLogger(Class<?> clazz);
	
	/**
	 * Returns the context path
	 * @return String
	 */
	public String getContextPath();
	
	/**
	 * Sets the module this context is created for
	 * @param module
	 */
	public void setModule(NightModule module);
	
	/**
	 * Returns the specified session type.
	 * 
	 * @see Context#setSessionType(Class)
	 * 
	 * @return Class<? extends WebSession> the type of session objects
	 */
	public Class<? extends WebSession> getSessionType();
	
	/**
	 * Sets the session type that should be used for the specific module.
	 * The module can extend the WebSession object in order to save variables
	 * specific for a session of a user logged in.
	 * 
	 * <p>Requires a public constructor with Context as the only parameter.<p>
	 * 
	 * @param  Class<? extends WebSession> the type of session objects
	 */
	public void setSessionType(Class<? extends WebSession> sessionType);
	
	/**
	 * Gets the context from a ServletRequest through one of its attributes,
	 * the internal classes need to set an attribute in the Request
	 * 
	 * @param ServletRequest the Request
	 * @return Context
	 */
	public static Context get(ServletRequest request) {
		Context ctx = (Context)request.getAttribute("context");
		
		return ctx;
	}
}
