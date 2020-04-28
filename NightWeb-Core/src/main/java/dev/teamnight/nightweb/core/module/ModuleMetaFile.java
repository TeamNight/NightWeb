/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.module;

import java.nio.file.Path;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "module")
public class ModuleMetaFile {

	private Path modulePath;

	@XmlElement
	private String moduleIdentifier;
	@XmlElement
	private String moduleName;
	
	@XmlElement
	private String version;
	
	@XmlElementWrapper(name = "dependencies")
	@XmlElement(name = "dependency")
	private List<String> dependencies;
	
	@XmlElementWrapper(name = "mavenDependencies")
	@XmlElement(name = "dependency")
	private List<String> mavenDependencies;
	
	@XmlElement(name = "Main-Class")
	private String mainClass;

	/**
	 * @return the modulePath
	 */
	public Path getModulePath() {
		return modulePath;
	}

	/**
	 * @return the moduleIdentifier
	 */
	public String getModuleIdentifier() {
		return moduleIdentifier;
	}

	/**
	 * @return the moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the dependencies
	 */
	public List<String> getDependencies() {
		return dependencies;
	}

	/**
	 * @return the mavenDependencies
	 */
	public List<String> getMavenDependencies() {
		return mavenDependencies;
	}

	/**
	 * @return the mainClass
	 */
	public String getMainClass() {
		return mainClass;
	}

	/**
	 * @param modulePath the modulePath to set
	 */
	public void setModulePath(Path modulePath) {
		this.modulePath = modulePath;
	}

	/**
	 * @param moduleIdentifier the moduleIdentifier to set
	 */
	public void setModuleIdentifier(String moduleIdentifier) {
		this.moduleIdentifier = moduleIdentifier;
	}

	/**
	 * @param moduleName the moduleName to set
	 */
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * @param mavenDependencies the mavenDependencies to set
	 */
	public void setMavenDependencies(List<String> mavenDependencies) {
		this.mavenDependencies = mavenDependencies;
	}

	/**
	 * @param mainClass the mainClass to set
	 */
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}
	
}
