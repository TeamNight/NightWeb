/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.SessionFactory;

public interface ApplicationContext extends Context {

	/**
	 * Returns the session factory created by the NightWeb instance.
	 * 
	 * @return {@link org.hibernate.SessionFactory} the SessionFactory
	 */
	public SessionFactory getSessionFactory();
	
	/**
	 * Registers a servlet with the specified context in order to associate a servlet holder with a context
	 * @param servlet
	 * @param pathSpec
	 * @param ctx
	 */
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathSpec, Context ctx);
	
	/**
	 * Registers a servlet holder
	 * 
	 * @param holder
	 * @param pathSpec
	 */
	public void registerServletHolder(ServletHolder holder, String pathSpec);
	
}
