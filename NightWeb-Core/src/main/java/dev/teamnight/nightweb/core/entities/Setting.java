/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "settings", uniqueConstraints = @UniqueConstraint(columnNames = {"key"}))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Setting {

	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable = false)
	private String key;
	
	@Column
	private String value;
	
	@Column(nullable = false)
	private String defaultValue;
	
	@Column(nullable = false)
	private Setting.Type type;	
	
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

	/**
	 * @return the type
	 */
	public Setting.Type getType() {
		return type;
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
	 * @param type the type to set
	 */
	public void setType(Setting.Type type) {
		this.type = type;
	}

	/**
	 * @author Jonas
	 *
	 */
	public enum Type {
		STRING,
		NUMBER,
		FLAG
	}
}
