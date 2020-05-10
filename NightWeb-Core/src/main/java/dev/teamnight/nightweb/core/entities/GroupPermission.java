/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "group_permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"groupId", "name"}))
public class GroupPermission extends Permission {
	
	@ManyToOne
	@JoinColumn(name = "groupId", nullable = false)
	private Group group;

	public GroupPermission(Group group, Permission permission) {
		super(permission.getName(), permission.getType(), permission.getValue(), permission.getModule());
		this.group = group;
	}
	
	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}
	
	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}
	
}
