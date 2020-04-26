package dev.teamnight.nightweb.core.module;

import java.nio.file.Path;
import java.util.List;

import dev.teamnight.nightweb.core.NightModule;

public interface ModuleManager {
	
	public void loadModules(Path modulesDir);
	
	public void loadModule(Path path);
	
	public NightModule getModuleBySimpleName(String name);
	
	public NightModule getModuleByIdentifier(String identifier);
	
	public List<NightModule> getEnabledModules();
	
	public List<NightModule> getLoadedModules();
	
	public boolean isModuleEnabled(String identifier);
	
	public boolean isModuleEnabled(NightModule module);
	
	public void registerLoader(Class<? extends ModuleLoader> loader) throws IllegalArgumentException;
}
