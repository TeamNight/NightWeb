/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import dev.teamnight.nightweb.core.entities.Permission.Tribool;
import dev.teamnight.nightweb.core.entities.Permission.Type;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "user_permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "name"}))
public class UserPermission extends Permission {

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private User user;
	
	protected UserPermission() {}
	
	public UserPermission(User user, Permission permission) {
		super(permission.getName(), permission.getType(), permission.getValue());
		this.user = user;
	}
	
	public UserPermission(User user, String name, short value) {
		this(user, name, Type.NUMBER, String.valueOf(value));
	}
	
	public UserPermission(User user, String name, int value) {
		this(user, name, Type.NUMBER, String.valueOf(value));
	}
	
	public UserPermission(User user, String name, long value) {
		this(user, name, Type.NUMBER, String.valueOf(value));
	}
	
	public UserPermission(User user, String name, double value) {
		this(user, name, Type.NUMBER, String.valueOf(value));
	}
	
	public UserPermission(User user, String name, Tribool value) {
		this(user, name, Type.FLAG, value.getAsString());
	}
	
	public UserPermission(User user, String name, Permission.Type type, String value) {
		super(name, type, value);
		this.user = user;
	}
	
}
