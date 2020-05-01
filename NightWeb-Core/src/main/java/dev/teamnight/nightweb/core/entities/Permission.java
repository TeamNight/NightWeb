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

@Entity
@Table(name = "permissions")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Permission {

	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private Permission.Type type;
	
	@Column(nullable = false)
	private String value;
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the type
	 */
	public Permission.Type getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	public long getAsLong() {
		return Long.parseLong(value);
	}
	
	public Tribool getAsTribool() {
		if(this.value.equalsIgnoreCase("allow")) {
			return Tribool.TRUE;
		} else if(this.value.equalsIgnoreCase("deny")) {
			return Tribool.FALSE;
		} else {
			return Tribool.NEUTRAL;
		}
	}
	
	public boolean getAsBoolean() {
		if(this.type == Type.FLAG) {
			Tribool bool = this.getAsTribool();
			
			if(bool == Tribool.TRUE) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Permission.Type type) {
		this.type = type;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
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
	
	public enum Tribool {
		TRUE("ALLOW"),
		NEUTRAL("UNSET"),
		FALSE("DENY");
		
		private String asString;
		
		private Tribool(String representation) {
			this.asString = representation;
		}
		
		public String getAsString() {
			return asString;
		}
	}
}
