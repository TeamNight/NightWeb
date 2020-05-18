/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import dev.teamnight.nightweb.core.template.FormattableString;

/**
 * @author Jonas
 *
 */
@XmlRootElement(name = "language")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlLanguageMap {

	@XmlAttribute(name = "languageCode")
	private String languageCode;
	
	@XmlAttribute(name = "languageName")
	private String localizedName;
	
	@XmlElement
	@XmlJavaTypeAdapter(XmlLanguageMapAdapter.class)
	private Map<String, XmlLanguageItem> map = new HashMap<String, XmlLanguageItem>();
	
	public XmlLanguageMap() {}
	
	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}
	
	/**
	 * @return the localizedName
	 */
	public String getLocalizedName() {
		return localizedName;
	}
	
	/**
	 * @return the map
	 */
	public Map<String, XmlLanguageItem> getMap() {
		return map;
	}
	
	/**
	 * Returns the language item associated with this key
	 * @param key
	 * @return String
	 */
	public XmlLanguageItem getItem(String key) {
		return this.map.get(key);
	}
	
	/**
	 * Returns the language string associated with that key
	 * @param key
	 * @return String
	 */
	public FormattableString getString(String key) {
		XmlLanguageItem item = this.map.get(key);
		
		if(item != null) {
			return new FormattableString(item.getValue());
		}
		return null;
	}
	
	/**
	 * @param languageCode the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	/**
	 * @param localizedName the localizedName to set
	 */
	public void setLocalizedName(String localizedName) {
		this.localizedName = localizedName;
	}
	
	/**
	 * @param map the map to set
	 */
	public void setMap(Map<String, XmlLanguageItem> map) {
		this.map = map;
	}
	
	/**
	 * Adds an item to the language map
	 * @param item
	 */
	public void addItem(XmlLanguageItem item) {
		this.map.put(item.getKey(), item);
	}
	
	/**
	 * Adds items of other languageMap to this
	 * @param item
	 */
	public void addItems(XmlLanguageMap otherMap) {
		this.map.putAll(otherMap.getMap());
	}
	
	public void removeItem(XmlLanguageItem item) {
		this.map.remove(item.getKey());
	}
	
	public void removeItem(String key) {
		this.map.remove(key);
	}
}
