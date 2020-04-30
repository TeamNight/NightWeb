/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "modules")
public class ModuleData {
	
	@Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	private long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "identifier", nullable = false, unique = true)
	private String identifier;
	
	@Column(name = "version")
	private String version;
	
	@Column(name = "path")
	private String path;
	
	@Column(name = "enabled", nullable = false)
	private boolean enabled;
	
	public ModuleData() {}
	
	public ModuleData(String identifier) {
		this.name = identifier;
		this.identifier = identifier;
		this.version = "0.0.0";
		this.enabled = false;
	}
	
	public ModuleData(ApplicationData data) {
		this.name = data.getName();
		this.identifier = data.getIdentifier();
		this.version = data.getVersion();
		this.enabled = false;
	}

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
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
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
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public String toString() {
		return String.format("ModuleData(id=%s, identifier=%s, version=%s)", this.id, this.identifier, this.version);
	}

}
