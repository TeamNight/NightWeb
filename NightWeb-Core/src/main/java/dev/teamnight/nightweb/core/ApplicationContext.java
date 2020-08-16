/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpServlet;

import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.util.ServletBuilder;

public interface ApplicationContext extends Context {

	/**
	 * Returns the session factory created by the NightWeb instance.
	 * 
	 * @return {@link org.hibernate.SessionFactory} the SessionFactory
	 */
	public SessionFactory getSessionFactory();
	
	/**
	 * @return {@link dev.teamnight.nightweb.core.ServletRegistrationAdapter} the adapter
	 */
	public ServletRegistrationAdapter getServletRegistrationAdapter();
	
	/**
	 * Registers a servlet with the specified context in order to associate a servlet holder with a context
	 * @param servlet
	 * @param pathSpec
	 * @param ctx
	 */
	public void addServlet(ServletBuilder builder, String pathSpec, Context ctx);
	
	/**
	 * Sets the servlet registration adapter, needed by the context.
	 * 
	 * @param {@link dev.teamnight.nightweb.core.ServletRegistrationAdapter} the adapter
	 */
	public void setServletRegistration(ServletRegistrationAdapter adapter);
	
}
