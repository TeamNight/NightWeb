/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.teamnight.nightweb.core.entities.XmlLanguageItem;
import dev.teamnight.nightweb.core.entities.XmlLanguageMap;

/**
 * @author Jonas
 *
 */
public class LanguageManagerImpl implements LanguageManager {

	private static final Logger LOGGER = LogManager.getLogger();
	
	private Map<String, XmlLanguageMap> languages = new HashMap<String, XmlLanguageMap>();

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;
	
	public LanguageManagerImpl() throws JAXBException {
		this.jaxbContext = JAXBContext.newInstance(XmlLanguageMap.class, XmlLanguageItem.class);
		this.unmarshaller = jaxbContext.createUnmarshaller();
	}
	
	@Override
	public void loadLanguages(Path path) {
		if(path == null || !Files.isDirectory(path)) {
			throw new IllegalArgumentException("path must be a directory");
		}
		
		LOGGER.info("Loading languages...");
		
		try {
			Files.list(path).forEach(item -> {
				LOGGER.debug("Loading language file: " + item);
				if(Files.isDirectory(item)) {
					return;
				}
				
				this.loadLanguage(item);
			});
		} catch (IOException e) {
			LOGGER.error("Error occured while reading language directory: " + e.getMessage());
			e.printStackTrace();
		}
		
		this.languages.forEach((string, map) -> {
			LOGGER.debug("Language contents for " + string);
			
			map.getMap().forEach((key, lang) -> {
				LOGGER.debug(key + ": " + lang);
			});
		});
	}

	@Override
	public void loadLanguage(Path path) {
		if(path == null || Files.isDirectory(path)) {
			throw new IllegalArgumentException("path must be a file");
		}
		
		LOGGER.info("Loading language >> " + path.getFileName());
		
		try {
			XmlLanguageMap map = (XmlLanguageMap) this.unmarshaller.unmarshal(path.toFile());
			
			if(this.languages.containsKey(map.getLanguageCode())) {
				XmlLanguageMap otherMap = this.languages.get(map.getLanguageCode());
				otherMap.addItems(map);
			} else {
				this.languages.put(map.getLanguageCode(), map);
			}
			
			LOGGER.info("Loaded language >> " + map.getLanguageCode() + "_" + map.getLocalizedName());
		} catch (JAXBException e) {
			LOGGER.error("Unable to load language \"" + path + "\": " + e.getMessage() + "(" + e.getErrorCode() + ")");
			e.printStackTrace();
		}
	}

	@Override
	public XmlLanguageMap getLanguageMap(String languageCode) {
		return this.languages.get(languageCode);
	}

	@Override
	public void addLanguageMap(XmlLanguageMap map) {
		this.languages.putIfAbsent(map.getLanguageCode(), map);
	}

	@Override
	public void removeLanguageMap(String languageCode) {
		this.languages.remove(languageCode);
	}

	@Override
	public void removeLanguageMap(XmlLanguageMap map) {
		this.languages.remove(map.getLanguageCode());
	}

	@Override
	public String[] getAvailableLanguages() {
		return this.languages.keySet().toArray(new String[0]);
	}

}
