/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.Servlet;

/**
 * A class to provide an easy builder for servlets and all features,
 * including defining how the class is created and setting important
 * values like the servlet's name in the servlet container and the
 * init paremeters.
 * 
 * The functionality of registration may depend on the embedded
 * servlet container used.
 * 
 * @author Jonas
 *
 */
public class ServletBuilder {

	private String servletName;
	private Map<String, String> initParameters = new HashMap<String, String>();
	
	private String className;
	private Class<? extends Servlet> clazz;
	private Servlet theServlet;
	
	public ServletBuilder(String className) {
		this.className = className;
	}
	
	public ServletBuilder(Class<? extends Servlet> servletClass) {
		this.clazz = servletClass;
	}
	
	public ServletBuilder(Servlet servlet) {
		this.theServlet = servlet;
	}
	
	/**
	 * @return the servletName
	 */
	public Optional<String> getServletName() {
		return Optional.ofNullable(servletName);
	}
	
	/**
	 * @param servletName the servletName to set
	 */
	public ServletBuilder servletName(String servletName) {
		this.servletName = servletName;
		
		return this;
	}
	
	/**
	 * @return the initParameters
	 */
	public Map<String, String> getInitParameters() {
		return initParameters;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getInitParameter(String key) {
		return initParameters.get(key);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	public ServletBuilder addInitParamter(String key, String value) {
		this.initParameters.put(key, value);
		
		return this;
	}
	
	/**
	 * @return the className
	 */
	public Optional<String> getClassName() {
		return Optional.ofNullable(this.className);
	}
	
	/**
	 * @return the clazz
	 */
	public Optional<Class<? extends Servlet>> getHeldClass() {
		return Optional.ofNullable(clazz);
	}
	
	/**
	 * @return the theServlet
	 */
	public Optional<Servlet> getServlet() {
		return Optional.ofNullable(theServlet);
	}
}
