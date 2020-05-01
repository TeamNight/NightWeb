/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

/**
 * @author Jonas
 *
 */

@Entity
@Table(name = "users")
public class User implements PermissionOwner<UserPermission> {

	@Id
	@GeneratedValue
	private long id;
	@Column(nullable = false)
	private String username;
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private Date registrationDate;
	@Column
	private Date lastLoginDate;
	
	@Column
	private String activationKey;
	@Column
	private String recoveryKey;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "user_groups",
			joinColumns = @JoinColumn(name = "groupId"),
			inverseJoinColumns = @JoinColumn(name = "userId")
			)
	private List<Group> groups;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
	@OrderBy("name ASC")
	private List<UserPermission> permissions;
	
	@Column(nullable = false)
	private boolean disabled;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the registrationDate
	 */
	public Date getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @return the lastLoginDate
	 */
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	/**
	 * @return the activationKey
	 */
	public String getActivationKey() {
		return activationKey;
	}

	/**
	 * @return the recoveryKey
	 */
	public String getRecoveryKey() {
		return recoveryKey;
	}

	/**
	 * @return the groups
	 */
	public List<Group> getGroups() {
		return groups;
	}

	/**
	 * @return the permissions
	 */
	@Override
	public List<UserPermission> getPermissions() {
		return permissions;
	}
	
	@Override
	public List<Permission> getInheritedPermissions() {
		List<Permission> groupPermissions = new ArrayList<Permission>();
		
		for(Group group : this.groups) {
			groupPermissions.addAll(group.getPermissions());
		}
		
		return groupPermissions;
	}

	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	/**
	 * @param lastLoginDate the lastLoginDate to set
	 */
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	/**
	 * @param activationKey the activationKey to set
	 */
	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	/**
	 * @param recoveryKey the recoveryKey to set
	 */
	public void setRecoveryKey(String recoveryKey) {
		this.recoveryKey = recoveryKey;
	}
	
	/**
	 * @param group the group to add
	 */
	public void addGroup(Group group) {
		this.groups.add(group);
	}
	
	/**
	 * @param group the group to remove
	 */
	public void removeGroup(Group group) {
		this.groups.remove(group);
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return this.hasPermission(permission.getName());
	}

	@Override
	public boolean hasPermission(String permission) {
		UserPermission userPerm = this.permissions.stream()
				.filter(perm -> perm.getName().equals(permission))
				.filter(perm -> perm.getAsBoolean())
				.findFirst()
				.orElse(null);
		
		if(userPerm != null) {
			return true;
		}
		
		List<GroupPermission> groupPermissions;
		
		return false;
	}

	@Override
	public void addPermission(Permission permission) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePermission(Permission permission) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearPermissions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPermissions(List<UserPermission> permissions) {
		// TODO Auto-generated method stub
		
	}
	
}
