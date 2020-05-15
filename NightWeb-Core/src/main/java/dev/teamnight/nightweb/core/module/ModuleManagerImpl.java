/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.module;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.NightWebCore;
import dev.teamnight.nightweb.core.entities.ModuleData;
import dev.teamnight.nightweb.core.entities.ModuleMetaFile;
import dev.teamnight.nightweb.core.exceptions.IllegalModuleIdentifierException;
import dev.teamnight.nightweb.core.exceptions.ModuleException;
import dev.teamnight.nightweb.core.impl.ModuleContext;
import dev.teamnight.nightweb.core.service.ModuleService;

public class ModuleManagerImpl implements ModuleManager {

	private static Logger LOGGER = LogManager.getLogger();
	
	private NightWebCore core;

	private List<ModuleHolder> loadedModules = new ArrayList<ModuleHolder>();
	
	private Map<Pattern, ModuleLoader> moduleLoaders = new HashMap<Pattern, ModuleLoader>();
	
	private List<LoadCustomizer> loadCustomizers = new ArrayList<LoadCustomizer>();
	
	public ModuleManagerImpl(NightWebCore web) {
		this.core = web;
	}
	
	@Override
	public void loadModules(Path modulesDir) throws IllegalArgumentException {
		// TODO Implement dependency loading
		
		if(!Files.isDirectory(modulesDir)) {
			throw new IllegalArgumentException(modulesDir.toAbsolutePath() + " is not a directory.");
		}
		
		List<Path> moduleFiles = null;
		try {
			moduleFiles = Files.list(modulesDir).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(moduleFiles == null) {
			return;
		}
		
		for(Path path : moduleFiles) {
			this.loadModule(path);
		}
	}

	@Override
	public void loadModule(Path path) throws ModuleException {
		//TODO Applications can not have dependencies
		
		LOGGER.info("Loading module at " + path);
		String filename = path.getFileName().toString();
		ModuleLoader loader = null;
		
		for(Pattern p : moduleLoaders.keySet()) {
			Matcher matcher = p.matcher(filename);
			
			LOGGER.debug("Current pattern: " + p.pattern());
			
			if(matcher.find()) {
				loader = moduleLoaders.get(p);
				LOGGER.debug("Found moduleLoader for " + path + ": " + loader.getClass().getCanonicalName());
			}
		}
		
		if(loader == null) {
			throw new ModuleException("Unable to get ModuleLoader for " + path.toAbsolutePath());
		}
		
		LOGGER.debug("Loading module from file " + path);
		NightModule module = loader.loadModule(path);
		LOGGER.debug("Getting meta file");
		ModuleMetaFile mf = loader.getModuleMetaFile(path);
		
		if(mf.getModuleIdentifier().isBlank() || mf.getModuleIdentifier().equalsIgnoreCase("dev.teamnight.nightweb.core")) {
			throw new IllegalModuleIdentifierException(); //If another module loader tried to ignore this case
		}
		
		//Set identifier is important
		module.setIdentifier(mf.getModuleIdentifier());
		
		ModuleHolder holder = new ModuleHolder();
		holder.setModule(module);
		holder.setMetaFile(mf);
		
		for(LoadCustomizer customizer : this.loadCustomizers) {
			customizer.customize(module);
		}
		
		LOGGER.info("Loaded module >> " + mf.getModuleIdentifier());
		
		if(this.getModuleByIdentifier(mf.getModuleIdentifier()) != null) {
			throw new IllegalModuleIdentifierException("Two modules with the name \"" + mf.getModuleIdentifier() + "\" found. This is not allowed");
		}
		
		this.loadedModules.add(holder);
	}

	@Override
	public NightModule getModuleBySimpleName(String name) {
		return this.loadedModules.stream().filter(holder -> holder.getMetaFile().getModuleName().equalsIgnoreCase(name)).map(holder -> holder.getModule()).findFirst().orElse(null);
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
	public void enableModules() throws ModuleException, UnknownDependencyException {
		List<Application> applications = this.loadedModules.stream()
				.map((holder) -> holder.getModule())
				.filter((module) -> module instanceof Application)
				.map((module) -> (Application)module)
				.collect(Collectors.toList());
		
		List<NightModule> modules = this.loadedModules.stream()
				.map((holder) -> holder.getModule())
				.filter((module) -> !(module instanceof Application))
				.collect(Collectors.toList());
		
		for(Application application : applications) {
			this.enableModule(application);
		}
		
		for(NightModule module : modules) {
			this.enableModule(module);
		}
	}

	@Override
	public boolean enableModule(NightModule module) throws ModuleException, UnknownDependencyException {
		if(module.isEnabled()) {
			return true;
		}
		
		LOGGER.debug("Enabling module (debug): " + module.getClass().getCanonicalName());
		
		ModuleHolder holder = this.loadedModules.stream().filter((mh) -> mh.getModule() == module).findFirst().orElse(null);
		
		if(holder == null) {
			throw new ModuleException("Module was removed?" + module.getClass().getCanonicalName());
		}
		
		LOGGER.info("Enabling module >> " + holder.getMetaFile().getModuleIdentifier());
		
		ModuleService modService = this.core.getServiceManager().getService(ModuleService.class);
		ModuleData data = modService.getByIdentifier(holder.getMetaFile().getModuleIdentifier());
		
		if(data == null) {
			LOGGER.warn("Could not enable module " + holder.getMetaFile().getModuleIdentifier() + ". Module is not installed.");
			return false;
		}
		
		//Add data to holder
		holder.setData(data);
		
		if(!data.isEnabled()) {
			LOGGER.warn("Could not enable module " + holder.getMetaFile().getModuleIdentifier() + ". Module is not enabled in DB.");
			return false;
		}
		
		List<String> dependencies = holder.getMetaFile().getDependencies();
		List<String> enabledDependencies = new ArrayList<String>();
		
		LOGGER.debug("Resolving dependencies");
		for(String dependencyName : dependencies) {
			LOGGER.debug("Dependency to be resolved: " + dependencyName);
			
			if(dependencyName.equalsIgnoreCase("dev.teamnight.nightweb.core")) {
				enabledDependencies.add(dependencyName);
				continue;
			}
			
			Optional<NightModule> dependency = this.loadedModules.stream()
					.filter(mh -> mh.getMetaFile().getModuleIdentifier().equals(dependencyName))
					.map(mh -> mh.getModule())
					.findFirst();
			
			if(dependency.isPresent()) {
				boolean enabledDep = this.enableModule(dependency.get());
				
				if(!enabledDep) {
					break;
				}
			} else {
				throw new UnknownDependencyException("Dependency " + dependencyName + " was not found. Please add it to the modules directory.");
			}
			
			enabledDependencies.add(dependencyName);
		}
		
		if(dependencies.size() != enabledDependencies.size()) {
			throw new ModuleException("Unable to enable all dependencies of " + holder.getMetaFile().getModuleIdentifier());
		}
		
		if(!(module instanceof Application)) {
			Application parentApplication = this.loadedModules.stream()
					.filter(mh -> dependencies.contains(mh.getMetaFile().getModuleIdentifier()))
					.map(mh -> mh.getModule())
					.filter(mod -> mod instanceof Application)
					.map(mod -> (Application)mod)
					.findFirst()
					.orElse(null);
			
			if(parentApplication == null) {
				if(dependencies.contains("dev.teamnight.nightweb.core")) {
					parentApplication = (Application) this.core;
				} else {
					throw new ModuleException("A module needs at least one application as dependency. Conflicting module is " + holder.getMetaFile().getModuleIdentifier());
				}
			}
			
			ApplicationContext appCtx = (ApplicationContext) parentApplication.getContext();
			
			ModuleContext ctx = new ModuleContext(appCtx);
			
			module.init(ctx);
			LOGGER.info("Enabled module >> " + holder.getMetaFile().getModuleIdentifier());
			return true;
		} else {
			Application app = (Application) module;
			
			LOGGER.debug("Application class: " + app.getClass().getCanonicalName());
			
			ApplicationContext ctx = null;
			try {
				ctx = this.core.getServer().getContext(app);
			} catch(IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			}
			
			app.init(ctx);
			LOGGER.info("Enabled application >> " + holder.getMetaFile().getModuleIdentifier());
			return true;
		}
	}
	
	@Override
	public void disableModule(NightModule module) {
		// TODO Implement
		throw new UnsupportedOperationException("Disabling modules is not implemented yet.");
	}
	
	@Override
	public void disableModules() {
		//TODO Implement
		throw new UnsupportedOperationException("Disabling modules is not implemented yet.");
	}
	
	@Override
	public Application getApplicationByName(String name) {
		for(ModuleHolder holder : this.loadedModules) {
			if(holder.getMetaFile().getModuleName().equals(name)) {
				if(holder.getModule() instanceof Application) {
					return (Application) holder.getModule();
				}
			}
		}
		return null;
	}
	
	@Override
	public Application getApplicationByIdentifier(String identifier) {
		for(ModuleHolder holder : this.loadedModules) {
			if(holder.getMetaFile().getModuleIdentifier().equals(identifier)) {
				if(holder.getModule() instanceof Application) {
					return (Application) holder.getModule();
				}
			}
		}
		return null;
	}
	
	@Override
	public List<Application> getApplications() {
		return this.loadedModules.stream()
				.filter((holder) -> holder.getModule() instanceof Application)
				.map((holder) -> (Application)holder.getModule())
				.collect(Collectors.toList());
	}

	@Override
	public void registerLoader(Class<? extends ModuleLoader> loader) throws IllegalArgumentException {
		ModuleLoader obj;
		
		try {
			obj = loader.getConstructor().newInstance();
			LOGGER.debug("Registering ModuleLoader: " + loader.getCanonicalName());
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

	@Override
	public void registerCustomizer(LoadCustomizer customizer) throws IllegalArgumentException {
		LOGGER.debug("Registering customizer: " + (customizer.getClass().getCanonicalName() != null 
				? customizer.getClass().getCanonicalName() 
						: customizer.getClass().getSimpleName()));
		this.loadCustomizers.add(customizer);
	}

	@Override
	public void installModule(NightModule module) {
		LOGGER.debug("Checking for ModuleService");
		ModuleService service = this.core.getServiceManager().getService(ModuleService.class);
		
		if(service == null) {
			throw new IllegalArgumentException("Modules can only be installed after the server was initialized");
		}
		
		LOGGER.debug("Checking for loaded module");
		ModuleHolder holder = this.loadedModules.stream()
				.filter(mh -> mh.getMetaFile().getModuleIdentifier().equals(module.getIdentifier()))
				.findFirst()
				.orElse(null);
		
		if(holder == null) {
			throw new IllegalArgumentException("Module " + module.getClass().getCanonicalName() + " is not loaded. Modules that are going to installed need to be loaded");
		}
		
		LOGGER.info("Installing module >> " + module.getIdentifier());
		
		LOGGER.debug("Checking for already installed module");
		ModuleData testData = service.getByIdentifier(module.getIdentifier());
		
		if(testData != null) {
			throw new IllegalArgumentException("Module " + module.getIdentifier() + " is already installed");
		}
		
		LOGGER.debug("Creating moduleData object and saving it");
		ModuleData data = new ModuleData();
		data.setIdentifier(holder.getMetaFile().getModuleIdentifier());
		data.setName(holder.getMetaFile().getModuleName());
		data.setVersion(holder.getMetaFile().getVersion());
		data.setPath(NightWeb.WORKING_DIR.relativize(holder.getMetaFile().getModulePath()).toString());
		data.setEnabled(true);
		
		service.save(data);
		LOGGER.info("Installed module >> " + data.getIdentifier());
		
		this.enableModule(module);
	}
	
	@Override
	public ModuleData getData(String identifier) {
		return this.loadedModules.stream()
				.filter(holder -> holder.getMetaFile().getModuleIdentifier().equalsIgnoreCase(identifier))
				.map(holder -> holder.getData())
				.findFirst()
				.orElse(null);
	}
	
	@Override
	public ModuleMetaFile getMeta(String identifier) {
		return this.loadedModules.stream()
				.filter(holder -> holder.getMetaFile().getModuleIdentifier().equalsIgnoreCase(identifier))
				.map(holder -> holder.getMetaFile())
				.findFirst()
				.orElse(null);
	}

}
