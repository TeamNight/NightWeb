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
	
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathSpec, Context ctx);
	
}
