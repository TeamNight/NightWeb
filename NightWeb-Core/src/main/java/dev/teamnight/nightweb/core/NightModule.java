/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.util.List;

import javax.servlet.http.HttpServlet;

import org.hibernate.Session;

public interface NightModule {
	
	/**
	 * Gets called in order to configure Hibernate
	 * 
	 * @param List<Class<?>> classList
	 */
	public void configure(List<Class<?>> entityList);
	/**
	 * This method gets called by NightWeb when it is time for
	 * the module to shine and set everything up.
	 * 
	 * @param {@link dev.teamnight.nightweb.core.Context} the Context
	 */
	public void init(Context ctx);
	
	/**
	 * @return {@link dev.teamnight.nightweb.core.Context} the Context
	 */
	public Context getContext();
	
	/**
	 * Adds a servlet to the handler.
	 * 
	 * <p>The Servlet needs to be annotated with a {@link javax.servlet.annotation.WebServlet} in order to be accepted.<p>
	 * 
	 * @param servlet
	 * @throws IllegalArgumentException if servlet is not annotated
	 */
	public void addServlet(HttpServlet servlet) throws IllegalArgumentException;
	
	/**
	 * Adds a servlet to the handler using given path.
	 * 
	 * @param servlet
	 * @param String path specification
	 */
	public void addServlet(HttpServlet servlet, String pathSpec);
	
	/**
	 * Adds a servlet to the handler.
	 * 
	 * <p>The Servlet needs to be annotated with a {@link javax.servlet.annotation.WebServlet} in order to be accepted.<p>
	 * 
	 * @param servlet
	 * @throws IllegalArgumentException if servlet is not annotated
	 */
	public void addServlet(Class<? extends HttpServlet> servlet) throws IllegalArgumentException;
	
	/**
	 * Adds a servlet to the handler using given path.
	 * 
	 * @param servlet
	 * @param String path specification
	 */
	public void addServlet(Class<? extends HttpServlet> servlet, String pathSpec);
	
	/**
	 * Returns a database session
	 * 
	 * @return {@link org.hibernate.Session} a database Session
	 */
	public Session getDatabase();
	
	/**
	 * @return boolean true if the module was enabled
	 */
	public boolean isEnabled();
	
}
