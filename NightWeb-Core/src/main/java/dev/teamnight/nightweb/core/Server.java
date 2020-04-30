/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import org.hibernate.SessionFactory;

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
	
	/**
	 * Creates an instance of ApplicationContext suitable for the server implementation.
	 * The ApplicationContext implementation shall be able to register Servlets so that
	 * Applications and Modules can register them.
	 * 
	 * @param {@link dev.teamnight.nightweb.core.Application} the Application to get an ApplicationContext
	 * @param String Base Path for the Application, e.g. /blog/
	 * @throws IllegalArgumentException if there is already an ApplicationContext set or if the Application is not registered in the Database
	 */
	ApplicationContext getContext(Application app) throws IllegalArgumentException;
	
}
