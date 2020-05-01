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
public class Group implements Comparable<Group>, PermissionOwner {
	
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
	
	public Group() {}
	
	public boolean equals(Group other) {
		if(this.id == other.id) {
			return true;
		}
		return false;
	}

	/**
	 * Defines the sort by priority -> highest priority, first object in List
	 */
	@Override
	public int compareTo(Group other) {
		if(this.priority >= other.priority) {
			if(this.priority == other.priority) {
				return 0;
			}
			return 1;
		} else {
			return -1;
		}
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
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	
}
