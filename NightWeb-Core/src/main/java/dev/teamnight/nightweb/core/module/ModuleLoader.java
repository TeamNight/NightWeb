package dev.teamnight.nightweb.core.module;

import java.nio.file.Path;
import java.util.regex.Pattern;

import dev.teamnight.nightweb.core.NightModule;

public interface ModuleLoader {

	/**
	 * Loads the module in the specified path,
	 * 	the Path must be a JAR file in case of Java Modules and can be a
	 * 	directory in case of other possible programming language modules
	 * 
	 * @param Path to the the module
	 * @return Module if the loader found a module.xml 
	 * 		and the Main Class implementing the Module interface
	 */
	public Module loadModule(Path path);
	
	/**
	 * Reads the module.xml descriptor file from the JAR archive or
	 * 	directory
	 * 
	 * @param Path to the module file or directory
	 * @return ModuleMeta metadata for the Module
	 */
	public ModuleMetaFile getModuleMetaFile(Path path);
	
	/**
	 * Initializes the module by calling its main method: 
	 * {@link dev.teamnight.nightweb.core.NightModule#init(dev.teamnight.nightweb.core.Context)}
	 * 
	 * @param NightModule the module to init
	 * <p>You should load the module in order to get the Module object</p>
	 */
	public void initModule(NightModule module);
	
	/**
	 * Installs a module to the database and calling {@link dev.teamnight.nightweb.core.NightModule#onInstall(SessionFactory factory)}
	 * 
	 * @param Path to the module file or directory
	 */
	public void installModule(Path path);
	
	/**
	 * Returns the file patterns when this loader should be called
	 * 
	 * @return Pattern[] file patterns
	 */
	public Pattern[] getFilePatterns();
	
}
