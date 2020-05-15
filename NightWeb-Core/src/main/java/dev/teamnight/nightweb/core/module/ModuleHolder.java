/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.module;

import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.entities.ModuleData;
import dev.teamnight.nightweb.core.entities.ModuleMetaFile;

public class ModuleHolder {

	private NightModule module;
	private ModuleData data;
	private ModuleMetaFile metaFile;
	
	/**
	 * @return the module
	 */
	public NightModule getModule() {
		return module;
	}
	
	/**
	 * @return the data
	 */
	public ModuleData getData() {
		return data;
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
	 * @param data the data to set
	 */
	public void setData(ModuleData data) {
		this.data = data;
	}
	
	/**
	 * @param metaFile the metaFile to set
	 */
	void setMetaFile(ModuleMetaFile metaFile) {
		this.metaFile = metaFile;
	}
	
	
	
}
