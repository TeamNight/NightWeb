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

@MappedSuperclass
public class Permission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(nullable = false)
	private Permission.Type type;
	
	@Column(nullable = false)
	private String value;
	
	@ManyToOne(optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "moduleId", nullable = false)
	private ModuleData module;
	
	protected Permission() {}
	
	public Permission(String name, short value, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), data);
	}
	
	public Permission(String name, int value, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), data);
	}
	
	public Permission(String name, long value, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), data);
	}
	
	public Permission(String name, double value, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), data);
	}
	
	public Permission(String name, Tribool value, ModuleData data) {
		this(name, Type.FLAG, value.getAsString(), data);
	}
	
	public Permission(String name, Permission.Type type, String value, ModuleData data) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.module = data;
	}
	
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
	 * @return the module
	 */
	public ModuleData getModule() {
		return module;
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
	 * @param module the module to set
	 */
	public void setModule(ModuleData module) {
		this.module = module;
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
	
	/**
	 * A tribool can contain 3 values, true, neutral and false.
	 * @author Jonas
	 *
	 */
	public enum Tribool {
		/**
		 * Allows a permission for a user
		 */
		TRUE("ALLOW"),
		/**
		 * Ignores (denies) a permission for a user.
		 * Depending on other groups, this might be a deny.
		 */
		NEUTRAL("UNSET"),
		/**
		 * Strongly denies a permission with no possibility for allowance.
		 */
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
