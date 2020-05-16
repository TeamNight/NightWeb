/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * @author Jonas
 *
 */

@Entity
@Table(name = "applications")
public class ApplicationData {

	@Id
	@GeneratedValue
	@Column(name = "id", updatable = false, nullable = false)
	private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "identifier", nullable = false, unique = true)
	private String identifier;
	
	@Column(name = "version")
	private String version;
	
	@Column(name = "contextPath", nullable = false)
	private String contextPath;
	
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OneToOne(cascade = CascadeType.ALL, optional = false)
	private ModuleData moduleData;
	
	public ApplicationData() {}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the contextPath
	 */
	public String getContextPath() {
		return contextPath;
	}
	
	/**
	 * @return the moduleData
	 */
	public ModuleData getModuleData() {
		return moduleData;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @param contextPath the contextPath to set
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	/**
	 * @param moduleData the moduleData to set
	 */
	public void setModuleData(ModuleData moduleData) {
		this.moduleData = moduleData;
	}
	
	@Override
	public String toString() {
		return String.format("ApplicationData(id=%s, identifier=%s, version=%s)", this.id, this.identifier, this.version);
	}
}
