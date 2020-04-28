/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.module;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightModule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JavaModuleLoader implements ModuleLoader {

	private static Logger LOGGER = LogManager.getLogger();
	
	private Pattern[] filePatterns;
	
	public JavaModuleLoader() {
		this.filePatterns = new Pattern[] { Pattern.compile("^.*\\.jar$") };
	}
	
	@Override
	public NightModule loadModule(Path path) throws IllegalArgumentException, ModuleException {
		if(path == null) {
			throw new IllegalArgumentException("Path can not be null");
		}
		
		LOGGER.debug("Loading meta file for " + path.toAbsolutePath());
		ModuleMetaFile metaFile = this.getModuleMetaFile(path);
		
		if(metaFile == null) {
			throw new ModuleException("Module.xml could not be loaded");
		}
		
		ModuleFinder finder = ModuleFinder.of(path.toAbsolutePath());
		ModuleLayer parent = ModuleLayer.boot();
		Configuration conf = parent.configuration().resolve(finder, ModuleFinder.of(), Set.of(metaFile.getModuleIdentifier()));
		ClassLoader scl = ClassLoader.getSystemClassLoader();
		ModuleLayer layer = parent.defineModulesWithOneLoader(conf, scl);
		parent.defineModulesWithOneLoader(conf, scl);
		
		try {
			Class<?> clazz = layer.findLoader(metaFile.getModuleIdentifier()).loadClass(metaFile.getMainClass());
			
			try {
				NightModule module = (NightModule) clazz.getConstructor().newInstance();
				
				return module;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new ModuleException(e, "Missing public " + clazz.getSimpleName() + "() constructor in " + metaFile.getMainClass());
			}
		} catch (ClassNotFoundException e) {
			throw new ModuleException(e, "Module does not contain main class " + metaFile.getMainClass());
		}
	}

	@Override
	public ModuleMetaFile getModuleMetaFile(Path path) throws IllegalArgumentException, ModuleException {
		if(path == null) {
			throw new IllegalArgumentException("Path can not be null");
		}
		
		try {
			JarFile jar = new JarFile(path.toAbsolutePath().toFile());
			JarEntry entry = jar.getJarEntry("module.xml");
			
			if(entry == null) {
				throw new ModuleException("Module.xml file is missing in module " + path.toAbsolutePath());
			}
			
			InputStream is = jar.getInputStream(entry);
			
			JAXBContext context = JAXBContext.newInstance(ModuleMetaFile.class);
			Unmarshaller um = context.createUnmarshaller();
			
			Object possibleMetaFile = um.unmarshal(is);
			
			if(possibleMetaFile instanceof ModuleMetaFile) {
				ModuleMetaFile metaFile = (ModuleMetaFile) possibleMetaFile;
				
				return metaFile;
			} else {
				throw new ModuleException("Module.xml is not in the needed format");
			}
		} catch (IOException | JAXBException e) {
			LOGGER.error("Error occured while loading Module Meta file from " + path.toAbsolutePath() + ": " + e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void initModule(NightModule module, Context ctx) throws IllegalArgumentException {
		module.init(ctx);
	}

	@Override
	public Pattern[] getFilePatterns() {
		return this.filePatterns.clone();
	}

}
