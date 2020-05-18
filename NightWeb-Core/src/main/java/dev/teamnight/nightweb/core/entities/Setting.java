/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * @author Jonas
 *
 */
@MappedSuperclass
public class Setting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "settingKey", nullable = false)
	private String key;
	
	@Column
	private String value;
	
	@Column(nullable = false)
	private String defaultValue;
	
	@Column(nullable = false)
	private Setting.Type type;	
	
	@Column(nullable = false)
	private String category = "general";
	
	@Column(nullable = false)
	private int showOrder = 0;
	
	@Column
	private String enumValues;
	
	@ManyToOne(optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "moduleId", nullable = false)
	private ModuleData module;
	
	public Setting() {}
	
	/**
	 * Constructor for a new setting, defaultValue will be the same as value
	 * @param key
	 * @param value
	 * @param type
	 */
	public Setting(String key, String value, Setting.Type type, ModuleData module) {
		this.key = key;
		this.value = value;
		this.defaultValue = value;
		this.type = type;
		this.module = module;
	}
	
	/**
	 * Constructor for a new setting, defaultValue will be the same as value
	 * @param key
	 * @param value
	 * @param type
	 */
	public Setting(String key, String value, Setting.Type type, String[] enumValues, ModuleData module) {
		this.key = key;
		this.value = value;
		this.defaultValue = value;
		this.type = type;
		this.enumValues = String.join(",", enumValues);
		this.module = module;
	}
	
	/**
	 * Copy constructor
	 * @param setting
	 */
	protected Setting(Setting setting) {
		this(setting.key, setting.value, setting.type, setting.module);
		this.defaultValue = setting.defaultValue;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	public boolean getAsBoolean() {
		if(this.type == Type.FLAG) {
			return Boolean.valueOf(this.value);
		}
		throw new IllegalStateException("This is not a flag setting.");
	}
	
	public long getAsLong() {
		if(this.type == Type.NUMBER) {
			return Long.parseLong(this.value);
		}
		throw new IllegalStateException("This is not a numeric setting.");
	}
	
	public int getAsInt() {
		if(this.type == Type.NUMBER) {
			return Integer.parseInt(this.value);
		}
		throw new IllegalStateException("This is not a numeric setting.");
	}
	
	public short getAsShort() {
		if(this.type == Type.NUMBER) {
			return Short.parseShort(this.value);
		}
		throw new IllegalStateException("This is not a numeric setting.");
	}
	
	public double getAsDouble() {
		if(this.type == Type.NUMBER) {
			return Double.parseDouble(this.value);
		}
		throw new IllegalStateException("This is not a numeric setting.");
	}
	
	public <E extends Enum<E>> Enum<E> getAsEnum(Class<E> enumType) {
		return Enum.valueOf(enumType, this.value);
	}
	
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return the type
	 */
	public Setting.Type getType() {
		return type;
	}
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * @return the enumValues
	 */
	public String[] getEnumValues() {
		return enumValues.contains(",") ? enumValues.split(",") : new String[] {enumValues};
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Setting.Type type) {
		this.type = type;
	}
	
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * @param enumValues the enumValues to set
	 */
	public void setEnumValues(String[] enumValues) {
		this.enumValues = String.join(",", enumValues);
	}

	/**
	 * @author Jonas
	 *
	 */
	public enum Type {
		STRING,
		NUMBER,
		FLAG,
		RADIOBUTTON,
		SELECTION,
		HIDDEN
	}
}
