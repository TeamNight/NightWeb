package dev.teamnight.nightweb.core.module;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import dev.teamnight.nightweb.core.NightModule;

public class ModuleManagerImpl implements ModuleManager {

	private List<ModuleHolder> loadedModules;
	
	private Map<Pattern, ModuleLoader> moduleLoaders;
	
	@Override
	public void loadModules(Path modulesDir) {
		// TODO Auto-generated method stub
	}

	@Override
	public void loadModule(Path path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NightModule getModuleBySimpleName(String name) {
		return null;
	}

	@Override
	public NightModule getModuleByIdentifier(String identifier) {
		for(ModuleHolder holder : this.loadedModules) {
			if(holder.getMetaFile().getModuleIdentifier().equals(identifier)) {
				return holder.getModule();
			}
		}
		return null;
	}
	
	@Override
	public List<NightModule> getEnabledModules() {
		List<NightModule> moduleList = new ArrayList<NightModule>();
		
		for(ModuleHolder holder : this.loadedModules) {
			if(holder.getModule().isEnabled())
				moduleList.add(holder.getModule());
		}
		
		return moduleList;
	}

	@Override
	public List<NightModule> getLoadedModules() {
		List<NightModule> moduleList = new ArrayList<NightModule>();
		
		for(ModuleHolder holder : this.loadedModules) {
			moduleList.add(holder.getModule());
		}
		
		return moduleList;
	}


	@Override
	public boolean isModuleEnabled(String identifier) {
		for(ModuleHolder holder : this.loadedModules) {
			if(holder.getMetaFile() != null) {
				if(identifier.equals(holder.getMetaFile().getModuleIdentifier())
						&& holder.getModule().isEnabled()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isModuleEnabled(NightModule module) {
		return module.isEnabled();
	}

	@Override
	public void registerLoader(Class<? extends ModuleLoader> loader) throws IllegalArgumentException {
		ModuleLoader obj;
		
		try {
			obj = loader.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			if(e instanceof NoSuchMethodException) {
				throw new IllegalArgumentException("ModuleLoader " + loader.getCanonicalName() + " does not have a public standard constructor.");
			}
			
			throw new IllegalArgumentException("Exception caught during registration of ModuleLoader: " + e.getMessage(), e);
		}
		
		for(Pattern pattern : obj.getFilePatterns()) {
			this.moduleLoaders.put(pattern, obj);
		}
	}

}
