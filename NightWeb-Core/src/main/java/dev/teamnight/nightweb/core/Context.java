/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.Router;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.template.TemplateBuilder;
import dev.teamnight.nightweb.core.template.TemplateManager;
import dev.teamnight.nightweb.core.util.ServletBuilder;

public interface Context {
	
	/**
	 * @return the identifier of the module associated with this context
	 */
	public String getModuleIdentifier();
	
	/**
	 * Returns the parent context or if it is the root context, the root context
	 * @return
	 */
	public Context getParent();
	
	/**
	 * Registers a servlet in the handler of the ApplicationContext.
	 * Modules do not get an own ContextHandler.
	 * 
	 * <p>Requires the WebServlet annotation with the urlPatterns parameter set
	 * 
	 * @param servlet
	 */
	public void addServlet(Class<? extends HttpServlet> servlet);
	
	/**
	 * Registers a servlet using a path specification.
	 * 
	 * <p>Do not requires the WebServlet annotation<p>
	 * 
	 * @see Context#registerServlet(Class)
	 * 
	 * @param servlet
	 * @param pathSpec
	 */
	public void addServlet(Class<? extends HttpServlet> servlet, String pathSpec);
	
	/**
	 * Registers a servlet using a path specification.
	 * 
	 * <p>Do not requires the WebServlet annotation<p>
	 * 
	 * @see Context#registerServlet(Class)
	 * 
	 * @param {@link dev.teamnight.nightweb.core.util.ServletBuilder}
	 * @param pathSpec
	 */
	public void addServlet(ServletBuilder builder, String pathSpec);
	
	/**
	 * Registers a controller in the router
	 * 
	 * @param {@link dev.teamnight.nightweb.core.mvc.Controller} controller
	 */
	public void addController(Controller controller);
	/**
	 * The router instance for this context
	 * 
	 * @return {@link dev.teamnight.nightweb.core.mvc.Router} the router
	 */
	public Router getRouter();
	
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
	 * @param HttpSession the Session of the Request
	 * @return {@link dev.teamnight.nightweb.core.Authenticator} the Authenticator for this context and the session provided
	 */
	public Authenticator getAuthenticator(HttpSession session);
	
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
