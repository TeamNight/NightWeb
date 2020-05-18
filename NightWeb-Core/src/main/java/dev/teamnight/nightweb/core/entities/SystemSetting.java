/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * A wrapper for the mapped superclass Setting
 * @author Jonas
 */
@Entity
@Table(name = "settings", uniqueConstraints = { @UniqueConstraint(columnNames = "settingKey") })
public class SystemSetting extends Setting {
	
	@ManyToOne(fetch = FetchType.EAGER)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "enabledBy")
	public SystemSetting enabledBy;
	
	public SystemSetting() {}
	
	/**
	 * Constructor for a new setting, defaultValue will be the same as value
	 * @param key
	 * @param value
	 * @param type
	 */
	public SystemSetting(String key, String value, Setting.Type type, ModuleData module) {
		super(key, value, type, module);
	}
	
	public SystemSetting(String key, String value, Setting.Type type, String category, ModuleData module) {
		super(key, value, type, module);
		this.setCategory(category);
	}
	
	public SystemSetting(String key, String value, Setting.Type type, String category, String[] enumValues, ModuleData module) {
		this(key, value, type, category, module);
		this.setEnumValues(enumValues);
	}
	
	/**
	 * Copy constructor
	 * @param setting
	 */
	protected SystemSetting(Setting setting) {
		super(setting);
	}
}
