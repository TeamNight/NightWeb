/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.util.List;

import dev.teamnight.nightweb.core.entities.ModuleData;
import dev.teamnight.nightweb.core.service.GroupService;
import dev.teamnight.nightweb.core.service.PermissionService;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.template.TemplateManager;

public interface NightWebCore {
	
	/**
	 * Returns the name of the Implementation of NightWeb
	 * 
	 * @return String implementationName
	 */
	public String getImplementationName();
	
	/**
	 * Version of NightWeb
	 * 
	 * @return String versionString
	 */
	public String getVersion();
	
	/**
	 * Returns the IP Address set in the configuration file of the server
	 * 
	 * @return
	 */
	public String getIPAddress();
	
	/**
	 * Returns the domain name set in the configuration file of the server
	 * 
	 * @return String domain
	 */
	public String getDomain();
	
	/**
	 * Returns the port of the server
	 * 
	 * @return int port
	 */
	public int getPort();
	
	/**
	 * Returns the server set that does the handling of HTTP requests in order
	 * to forward it to the servlets of the Applications and Modules
	 * 
	 * @return Server theServer
	 */
	public Server getServer();
	
	/**
	 * Sets the server in the NightWeb Implementation to the specific server wanted,
	 * the server accepts requests and handles them for the servlets, but also
	 * creates the ApplicationContext for the Applications in order to deal with the
	 * server.
	 * 
	 * <p>WARNING: The server can only be set during startup time, modules wanting
	 * to replace the server need to implement a static method in the following
	 * signature: public static void main(NightWeb)<p>
	 * 
	 * @param server
	 * @throws IllegalArgumentException
	 */
	public void setServer(Server server) throws IllegalArgumentException;
	
	/**
	 * Returns the service Manager that holds all accessible services at runtime
	 * 
	 * @return {@link dev.teamnight.nightweb.core.services.ServiceManager} serviceManager
	 */
	public ServiceManager getServiceManager();
	
	/**
	 * Returns the template Manager
	 * @return
	 */
	public TemplateManager getTemplateManager();
	
	// TODO Change to MailService
	public String getMailService();
	
	/**
	 * @return {@link dev.teamnight.nightweb.core.services.UserService}
	 */
	public UserService getUserService();
	
	/**
	 * @return {@link dev.teamnight.nightweb.core.services.GroupService}
	 */
	public GroupService getGroupService();
	
	/**
	 * @return {@link dev.teamnight.nightweb.core.services.PermissionService}
	 */
	public PermissionService getPermissionService();
	
	/**
	 * Returns a list of all active sessions of the web server
	 * 
	 * @return List of {@link dev.teamnight.nightweb.core.WebSession}
	 */
	public List<WebSession> getSessions();
	
	/**
	 * Adds a session to the session list
	 * @param session
	 */
	public void addSession(WebSession session);
	
	/**
	 * Removes a session from the session list
	 * @param session
	 */
	public void removeSession(WebSession session);
	
	/**
	 * Returns whether the debug mode is enabled
	 * @return
	 */
	public boolean isDebugModeEnabled();
	
	/**
	 * Gets the module data of the module of the specified identifier
	 * @param identifier
	 * @return
	 */
	public ModuleData getModuleData(String identifier);
	
	/**
	 * Gets the module data of the specified module
	 * @param module
	 * @return
	 */
	public ModuleData getModuleData(NightModule module);
}
