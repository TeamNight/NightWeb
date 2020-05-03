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
@Table(name = "user_permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "name"}))
public class UserPermission extends Permission {

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private User user;
	
}
