/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import dev.teamnight.nightweb.core.entities.Permission.Tribool;

import javax.persistence.JoinColumn;

/**
 * @author Jonas
 *
 */

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements PermissionOwner<UserPermission> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	private long id;
	@Column(nullable = false, unique = true)
	private String username;
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String salt;
	
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
	
	protected User() {}
	
	public User(String username, String email) {
		this.username = username;
		this.email = email;
		this.registrationDate = new Date();
		this.groups = new ArrayList<Group>();
		this.permissions = new ArrayList<UserPermission>();
		this.disabled = true;
	}

	// ----------------------------------------------------------------------- //
	// Getter/Setter                                                           //
	// ----------------------------------------------------------------------- //
	
	/**
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
	 * @return the salt
	 */
	public String getSalt() {
		return salt;
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
		Collections.sort(this.groups);
		return groups;
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
	 * @param salt the salt to set
	 */
	public void setSalt(String salt) {
		this.salt = salt;
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
	
	// ----------------------------------------------------------------------- //
	// PermissionOwner implemenation                                           //
	// ----------------------------------------------------------------------- //

	/**
	 * @return the permissions
	 */
	@Override
	public List<UserPermission> getPermissions() {
		return Collections.unmodifiableList(permissions);
	}
	
	@Override
	public List<Permission> getInheritedPermissions() {
		List<Permission> groupPermissions = new ArrayList<Permission>();
		
		for(Group group : this.groups) {
			groupPermissions.addAll(group.getPermissions());
		}
		
		return Collections.unmodifiableList(groupPermissions);
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return this.hasPermission(permission.getName());
	}

	@Override
	public boolean hasPermission(String permission) {
		/**
		 * Check for User
		 * UserPermission has yes or no/neutral, no resolves to true, no/neutral to no, UserPermissions override all GroupPermissions, if no go to GroupPerms
		 * (Groups are sorted by priority, highest priority first, all permissions to one ArrayList)
		 * GroupPermission true allows, neutral ignores, false denies
		 */
		UserPermission userPerm = this.permissions.stream()
				.filter(perm -> perm.getName().equals(permission))
				.filter(perm -> perm.getType() == Permission.Type.FLAG)
				.filter(perm -> perm.getAsBoolean())
				.findFirst()
				.orElse(null);
		
		if(userPerm != null) {
			return true;
		}
		
		boolean allow = false;
		
		List<GroupPermission> groupPermissions = new ArrayList<GroupPermission>();
		
		this.groups.forEach(group -> {
			groupPermissions.addAll(group.getPermissions().stream().filter(perm -> perm.getType() == Permission.Type.FLAG).collect(Collectors.toList()));
		});
		
		for(GroupPermission perm : groupPermissions) {
			Tribool bool = perm.getAsTribool();
			
			if(bool == Tribool.TRUE) {
				allow = true;
			} else if(bool == Tribool.FALSE) {
				return false;
			}
		}
		return allow;
	}
	
	@Override
	public UserPermission getPermission(String permissionName) {
		return this.permissions.stream().filter(perm -> perm.getName().equalsIgnoreCase(permissionName)).findFirst().orElse(null);
	}

	@Override
	public void addPermission(UserPermission permission) {
		UserPermission existingPerm = this.permissions.stream()
				.filter(perm -> perm.getName().equalsIgnoreCase(permission.getName()))
				.findFirst()
				.orElse(null);
		
		if(existingPerm != null) {
			permission.setName(permission.getName().toLowerCase());
			this.permissions.add(permission);
		}
	}

	@Override
	public void removePermission(UserPermission permission) {
		this.permissions.remove(permission);
	}
	
	@Override
	public void removePermission(String permissionName) {
		UserPermission permission = this.permissions.stream()
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
	public void setPermissions(List<UserPermission> permissions) {
		this.permissions = permissions;
	}
	
	// ----------------------------------------------------------------------- //
	// Authentication                                                          //
	// ----------------------------------------------------------------------- //
	
	/**
	 * Generates a sha-256 hash using the given password and the salt from the database
	 * 
	 * @param String unhashed password
	 * @return hashed password
	 */
	public String createHash(String password) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest((password + this.salt).getBytes(StandardCharsets.UTF_8));
			
			return String.format("%064x", new BigInteger(1, hash));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Verifies the user by his password.
	 * 
	 * @param String unhashed password
	 * @return true if the password equals
	 */
	public boolean verifyPassword(String passwordToCheck) {
		String hashedPassword = this.createHash(passwordToCheck);
		
		if(hashedPassword.equals(this.password)) {
			return true;
		}
		
		return false;
	}
}
