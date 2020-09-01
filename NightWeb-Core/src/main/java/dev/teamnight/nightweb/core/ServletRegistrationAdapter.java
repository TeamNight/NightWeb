/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import dev.teamnight.nightweb.core.util.ServletBuilder;

/**
 * A class providing a neutral adapter between the
 * handlers of the underlying server implementation
 * and the NightWeb core.
 * 
 * @author Jonas
 */
public interface ServletRegistrationAdapter {
	
	public String getContextPath();
	
	/**
	 * @return the Servlets registered to this ServletRegistrationAdapter.
	 */
	public List<HttpServlet> getServlets();
	/**
	 * <p>The List may be empty, if the server is not started yet.</p>
	 * 
	 * @return the Filters registered to this ServletRegistrationAdapter.
	 */
	public List<Filter> getFilters();
	
	/**
	 * Register a servlet to listen on a specific path.
	 * 
	 * @param {@link java.lang.Class<? extends javax.servlet.http.HttpServlet>} the Servlet
	 * @param String the pathSpec
	 */
	public void addServlet(Class<? extends HttpServlet> servlet, String path);
	/**
	 * Registers a servlet to listen on a specific path.
	 * 
	 * @param String the full name of the class
	 * @param String the pathSpec
	 */
	public void addServlet(String className, String path);
	
	/**
	 * Register a servlet to listen on a specific path.
	 * 
	 * @param {@link dev.teamnight.nightweb.core.util.ServletBuilder} the ServletBuilder
	 * @param String the pathSpec
	 */
	public void addServlet(ServletBuilder builder, String path);
	
	/**
	 * Registers a filter to listen on a specific path.
	 * 
	 * @param {@link java.lang.Class<? extends javax.servlet.Filter>} the Filter
	 * @param path
	 */
	public void addFilter(Class<? extends Filter> filter, String path, EnumSet<DispatcherType> dispatches);
	/**
	 * Registers a filter to listen on a specific path.
	 * 
	 * @param String the full name of the class
	 * @param String the pathSpec
	 */
	public void addFilter(String className, String path, EnumSet<DispatcherType> dispatches);
	
	/**
	 * Registers a filter to listen on a specific path.
	 * 
	 * @param {@link javax.servlet.Filter} the Filter
	 * @param path
	 */
	public void addFilter(Filter filter, String path, EnumSet<DispatcherType> dispatches);
	
	/**
	 * Sets the hostnames for the server to listen on.
	 * 
	 * e.g. forum.teamnight.dev
	 * 
	 * @param String[] the hostnames
	 */
	public void setHostnames(String[] hostnames);
	
	/**
	 * Sets the path registry for the ServletRegistrationAdapter.
	 * 
	 * The PathRegistry can only be set once unless it is not null.
	 * The ServletRegistrationAdapter can register the filters and
	 * the servlets in the PathRegistry for lookup.
	 * 
	 * @param {@link dev.teamnight.nightweb.core.PathRegistry} the path registry to set
	 */
	public void setPathRegistry(PathRegistry pathRegistry);

}
