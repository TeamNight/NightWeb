/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.module;

import java.nio.file.Path;
import java.util.List;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.NightModule;

public interface ModuleManager {
	
	public void loadModules(Path modulesDir) throws IllegalArgumentException;
	
	public void loadModule(Path path) throws ModuleException;
	
	public NightModule getModuleBySimpleName(String name);
	
	public NightModule getModuleByIdentifier(String identifier);
	
	public List<NightModule> getEnabledModules();
	
	public List<NightModule> getLoadedModules();
	
	public boolean isModuleEnabled(String identifier);
	
	public boolean isModuleEnabled(NightModule module);
	
	public void enableModules() throws ModuleException, UnknownDependencyException;
	
	public void enableModule(NightModule module) throws ModuleException, UnknownDependencyException;
	
	public void disableModule(NightModule module);
	
	public void disableModules();
	
	public Application getApplicationByName(String name);
	
	public Application getApplicationByIdentifier(String identifier);
	
	public List<Application> getApplications();
	
	public void registerLoader(Class<? extends ModuleLoader> loader) throws IllegalArgumentException;
	
	public void registerCustomizer(LoadCustomizer customizer) throws IllegalArgumentException;
	
}