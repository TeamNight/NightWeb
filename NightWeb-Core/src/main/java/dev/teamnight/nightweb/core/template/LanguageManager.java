/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

import java.nio.file.Path;

import dev.teamnight.nightweb.core.entities.XmlLanguageMap;

/**
 * @author Jonas
 *
 */
public interface LanguageManager {
	
	public void loadLanguages(Path path);
	
	public void loadLanguage(Path path);
	
	public XmlLanguageMap getLanguageMap(String languageCode);
	
	public void addLanguageMap(XmlLanguageMap map);
	
	public void removeLanguageMap(String languageCode);
	
	public void removeLanguageMap(XmlLanguageMap map);
	
}
