/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.entities.ApplicationData;

public interface Server {

	/**
	 * Method to command the server to start listening on previous set IP Address and Port
	 */
	public void start();
	
	/**
	 * Method to stop the server
	 */
	public void stop();
	
	/**
	 * @return List of active {@link javax.servlet.http.HttpSession}
	 */
	public List<HttpSession> getSessions();
	
	/**
	 * Creates an instance of ServletRegistrationAdapter suitable to the server implementation.
	 * 
	 * @param {@link dev.teamnight.nightweb.core.ApplicationContext} the ApplicationContext
	 * @throws IllegalArgumentException if there is already a ServletRegistrationAdapter
	 */
	public ServletRegistrationAdapter getServletRegistration(ApplicationContext appContext, ApplicationData data) throws IllegalArgumentException;
	
	/**
	 * Returns the mapping for the specified servlet in order to access the URL right, implements context path and the mapping without ending slash or *.
	 * @param Class<? extends HttpServlet> servletClass
	 * @return String the URL
	 */
	public String getServletURL(Class<? extends HttpServlet> servletClass);
	
	/**
	 * Returns the mapping for the specified servlet in order to access the URL right, implements context path and the mapping without ending slash or *.
	 * @param Class<? extends HttpServlet> servletClass
	 * @return String the URL
	 */
	public String getServletURL(String servletClass);
	
	/**
	 * Sets the ip address the server shall listen
	 * @param String the IP Address
	 */
	public void setIPAddress(String hostAddress);
	
	/**
	 * Sets the domain name the server shall allow to accept requests
	 * @param domainName
	 */
	public void setDomain(String domainName, String... additionalDomains);
	
	/**
	 * Sets the port
	 * @param port
	 */
	public void setPort(int port);
	
	/**
	 * Sets the port for SSL connections
	 * @param port
	 */
	public void setSSLPort(int port);
	
	/**
	 * Sets the session factory needed for ApplicationContexts
	 * @param factory
	 */
	public void setSessionFactory(SessionFactory factory);
	
}
