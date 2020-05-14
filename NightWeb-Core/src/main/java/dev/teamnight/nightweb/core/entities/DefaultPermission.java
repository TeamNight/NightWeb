/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class DefaultPermission extends Permission {
	
	protected DefaultPermission() {}
	
	public DefaultPermission(String name, short value, ModuleData data) {
		super(name, Type.NUMBER, String.valueOf(value), data);
	}
	
	public DefaultPermission(String name, int value, ModuleData data) {
		super(name, Type.NUMBER, String.valueOf(value), data);
	}
	
	public DefaultPermission(String name, long value, ModuleData data) {
		super(name, Type.NUMBER, String.valueOf(value), data);
	}
	
	public DefaultPermission(String name, double value, ModuleData data) {
		super(name, Type.NUMBER, String.valueOf(value), data);
	}
	
	public DefaultPermission(String name, Tribool value, ModuleData data) {
		super(name, Type.FLAG, value.getAsString(), data);
	}
}
