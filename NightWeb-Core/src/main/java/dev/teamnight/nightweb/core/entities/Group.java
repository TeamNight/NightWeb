/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "groups")
public class Group {
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column
	private String name;
	
	@Column
	private int priority;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "group")
	@OrderBy("name ASC")
	private List<GroupPermission> permissions;
}
