/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
public class Group implements Comparable<Group>, PermissionOwner<GroupPermission> {
	
	@Id
	@GeneratedValue
	@Column(updatable = false, nullable = false)
	private long id;
	
	@Column(length = 512, nullable = false, unique = true)
	private String name;
	
	@Column
	private int priority;
	
	@Column
	private boolean staffGroup;
	
	@Column
	private String bannerStyleClass;
	@Column
	private String bannerText;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "group")
	@OrderBy("name ASC")
	private List<GroupPermission> permissions;
	
	protected Group() {}
	
	public Group(String name) {
		this.name = name;
		this.staffGroup = false;
		this.permissions = new ArrayList<GroupPermission>();
	}
	
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
	 * @return the staffGroup
	 */
	public boolean isStaffGroup() {
		return staffGroup;
	}
	
	/**
	 * @return the bannerStyleClass
	 */
	public String getBannerStyleClass() {
		return bannerStyleClass;
	}
	
	/**
	 * @return the bannerText
	 */
	public String getBannerText() {
		return bannerText;
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
	
	/**
	 * @param staffGroup the staffGroup to set
	 */
	public void setStaffGroup(boolean staffGroup) {
		this.staffGroup = staffGroup;
	}
	
	/**
	 * @param bannerStyleClass the bannerStyleClass to set
	 */
	public void setBannerStyleClass(String bannerStyleClass) {
		this.bannerStyleClass = bannerStyleClass;
	}
	
	/**
	 * @param bannerText the bannerText to set
	 */
	public void setBannerText(String bannerText) {
		this.bannerText = bannerText;
	}
	
	// ----------------------------------------------------------------------- //
	// PermissionOwner implemenation                                           //
	// ----------------------------------------------------------------------- //
	

	@Override
	public List<GroupPermission> getPermissions() {
		return Collections.unmodifiableList(permissions);
	}
	
	@Override
	public List<Permission> getInheritedPermissions() {
		return this.permissions.stream().map(perm -> (Permission)perm).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return this.hasPermission(permission.getName());
	}

	@Override
	public boolean hasPermission(String permissionName) {
		GroupPermission permission = this.permissions.stream()
				.filter(perm -> perm.getName().equalsIgnoreCase(permissionName))
				.filter(perm -> perm.getType() == Permission.Type.FLAG)
				.filter(perm -> perm.getAsBoolean())
				.findFirst()
				.orElse(null);
		
		if(permission != null) {
			return true;
		}
		return false;
	}
	
	@Override
	public GroupPermission getPermission(String permissionName) {
		return this.permissions.stream().filter(perm -> perm.getName().equalsIgnoreCase(permissionName)).findFirst().orElse(null);
	}

	@Override
	public void addPermission(GroupPermission permission) {
		GroupPermission existingPerm = this.permissions.stream()
				.filter(perm -> perm.getName().equalsIgnoreCase(permission.getName()))
				.findFirst()
				.orElse(null);
		
		if(existingPerm != null) {
			permission.setName(permission.getName().toLowerCase());
			this.permissions.add(permission);
		}
	}

	@Override
	public void removePermission(GroupPermission permission) {
		this.permissions.remove(permission);
	}
	
	@Override
	public void removePermission(String permissionName) {
		GroupPermission permission = this.permissions.stream()
				.filter(perm -> perm.getName().equalsIgnoreCase(permissionName))
				.findFirst()
				.orElse(null);
		
		if(permission != null) {
			this.permissions.remove(permission);
		}
	}

	@Override
	public void clearPermissions() {
		this.permissions.clear();
	}

	@Override
	public void setPermissions(List<GroupPermission> permissions) {
		this.permissions = permissions;
	}
	
}
