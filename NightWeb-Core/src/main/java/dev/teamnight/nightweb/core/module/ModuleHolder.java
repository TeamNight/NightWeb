/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.module;

import dev.teamnight.nightweb.core.NightModule;

public class ModuleHolder {

	private NightModule module;
	private ModuleMetaFile metaFile;
	
	/**
	 * @return the module
	 */
	public NightModule getModule() {
		return module;
	}
	
	/**
	 * @return the metaFile
	 */
	public ModuleMetaFile getMetaFile() {
		return metaFile;
	}
	
	/**
	 * @param module the module to set
	 */
	void setModule(NightModule module) {
		this.module = module;
	}
	
	/**
	 * @param metaFile the metaFile to set
	 */
	void setMetaFile(ModuleMetaFile metaFile) {
		this.metaFile = metaFile;
	}
	
	
	
}
