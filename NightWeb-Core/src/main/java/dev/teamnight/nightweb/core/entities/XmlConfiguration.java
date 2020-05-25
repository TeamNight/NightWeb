/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlConfiguration {

	@XmlElement(name="developerMode")
	private boolean debug;
	
	@XmlElement
	private String hostIPAddress;
	@XmlElement
	private String domain;
	
	@XmlElement
	private int port;
	@XmlElement
	private int sslPort;
	
	@XmlElement(name = "webServerMinimumThreads")
	private int minThreads;
	@XmlElement(name = "webServerMaximumThreads")
	private int maxThreads;
	
	@XmlElement
	private int templateCacheSize;
	
	@XmlElement
	private String keystoreFile;
	@XmlElement
	private String keystorePassword;
	
	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}
	/**
	 * @return the hostIPAddress
	 */
	public String getHostIPAddress() {
		return hostIPAddress;
	}
	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @return the sslPort
	 */
	public int getSslPort() {
		return sslPort;
	}
	/**
	 * @return the minThreads
	 */
	public int getMinThreads() {
		return minThreads;
	}
	/**
	 * @return the maxThreads
	 */
	public int getMaxThreads() {
		return maxThreads;
	}
	/**
	 * @return the keystoreFile
	 */
	public String getKeystoreFile() {
		return keystoreFile;
	}
	/**
	 * @return the keystorePassword
	 */
	public String getKeystorePassword() {
		return keystorePassword;
	}
	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	/**
	 * @param hostIPAddress the hostIPAddress to set
	 */
	public void setHostIPAddress(String hostIPAddress) {
		this.hostIPAddress = hostIPAddress;
	}
	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @param sslPort the sslPort to set
	 */
	public void setSslPort(int sslPort) {
		this.sslPort = sslPort;
	}
	/**
	 * @param minThreads the minThreads to set
	 */
	public void setMinThreads(int minThreads) {
		this.minThreads = minThreads;
	}
	/**
	 * @param maxThreads the maxThreads to set
	 */
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}
	/**
	 * @param keystoreFile the keystoreFile to set
	 */
	public void setKeystoreFile(String keystoreFile) {
		this.keystoreFile = keystoreFile;
	}
	/**
	 * @param keystorePassword the keystorePassword to set
	 */
	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}
	
}
