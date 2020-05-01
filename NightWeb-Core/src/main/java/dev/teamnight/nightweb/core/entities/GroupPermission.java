/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "group_permissions")
public class GroupPermission extends Permission {
	
	@ManyToOne
	@JoinColumn(name = "groupId", nullable = false)
	private Group group;

}
