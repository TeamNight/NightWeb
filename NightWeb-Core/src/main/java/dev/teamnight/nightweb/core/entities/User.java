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
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedAttributeNode;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.logging.log4j.LogManager;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import dev.teamnight.nightweb.core.entities.Permission.Tribool;

/**
 * @author Jonas
 *
 */

@Entity
@Table(name = "users")
@NamedEntityGraph(name = "graph.User", attributeNodes = {
		@NamedAttributeNode("groups"),
		@NamedAttributeNode("permissions")
})
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
	
	@ManyToMany
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinTable(
			name = "user_groups",
			joinColumns = @JoinColumn(name = "userId"),
			inverseJoinColumns = @JoinColumn(name = "groupId"),
			uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "groupId"}))
	private List<Group> groups = new ArrayList<Group>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
	@OrderBy("name ASC")
	private List<UserPermission> permissions = new ArrayList<UserPermission>();
	
	@Column(name = "language")
	private String languageCode;
	
	@Column
	private boolean banned = false;
	@Column
	private String banReason;
	@Column
	private Date banExpiration;
	
	@Column(nullable = false)
	private boolean disabled;
	
	protected User() {}
	
	/**
	 * Constructor for the creation of a user
	 * Sets username, email, registrationDate to current time, a new groups list, a new permissions list, disabled to true and the languageCode to English
	 * @param username
	 * @param email
	 */
	public User(String username, String email) {
		this.username = username;
		this.email = email;
		this.registrationDate = new Date();
		this.disabled = true;
		this.languageCode = Locale.ENGLISH.toLanguageTag();
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
		return this.groups;
	}
	
	/**
	 * @return the languageCode
	 */
	public String getLanguageCode() {
		return languageCode;
	}
	
	/**
	 * @return the banned
	 */
	public boolean isBanned() {
		return banned;
	}
	
	/**
	 * @return the banReason
	 */
	public String getBanReason() {
		return banReason;
	}
	
	/**
	 * @return the banExpiration
	 */
	public Date getBanExpiration() {
		return banExpiration;
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
		Group g = this.groups.stream()
				.filter(listedGroup -> listedGroup.getId() == group.getId())
				.findAny()
				.orElse(null);
		
		if(g != null) {
			throw new IllegalArgumentException("Group already in groups list. Check before adding already known group");
		}
		
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
	 * @param languageCode the languageCode to set
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	/**
	 * @param banned the banned to set
	 */
	public void setBanned(boolean banned) {
		this.banned = banned;
	}
	
	/**
	 * @param banReason the banReason to set
	 */
	public void setBanReason(String banReason) {
		this.banReason = banReason;
	}
	
	/**
	 * @param banExpiration the banExpiration to set
	 */
	public void setBanExpiration(Date banExpiration) {
		this.banExpiration = banExpiration;
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
				.filter(perm -> perm.getName().equalsIgnoreCase(permission))
				.filter(perm -> perm.getType() == Permission.Type.FLAG)
				.filter(perm -> perm.getAsBoolean())
				.findFirst()
				.orElse(null);
		
		LogManager.getLogger().debug("UserPermission:null:" + (userPerm == null));
		LogManager.getLogger().debug("UserPermission:1:" + (userPerm != null ? userPerm.getAsBoolean() : "null"));
		
		if(userPerm != null) {
			return true;
		}
		
		boolean allow = false;
		
		List<GroupPermission> groupPermissions = new ArrayList<GroupPermission>();
		
		this.groups.forEach(group -> {
			groupPermissions.addAll(
					group.getPermissions().stream()
						.filter(perm -> perm.getType() == Permission.Type.FLAG)
						.filter(perm -> perm.getName().equalsIgnoreCase(permission))
						.collect(Collectors.toList())
					);
		});
		
		for(GroupPermission perm : groupPermissions) {
			Tribool bool = perm.getAsTribool();
			
			LogManager.getLogger().debug("GroupPermission:" + perm.getName() + ":" + perm.getAsBoolean() + ":" + perm.getAsTribool());
			LogManager.getLogger().debug("allow:1:" + allow);
			
			if(bool == Tribool.TRUE) {
				allow = true;
			} else if(bool == Tribool.FALSE) {
				return false;
			}
		}
		
		LogManager.getLogger().debug("allow:" + allow);
		
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
		
		if(existingPerm == null) {
			permission.setName(permission.getName());
			this.permissions.add(permission);
		} else {
			LogManager.getLogger().debug("Updating user perm " + existingPerm.getName() + " to " + permission.getValue());
			existingPerm.setValue(permission.getValue());
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
			LogManager.getLogger().debug("Removing permission: " + permission.getName());
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
		if(this.salt == null || this.salt.isBlank()) {
			this.generateSalt();
		}
		
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
	
	
	/**
	 * Generates a salt for the user
	 */
	private void generateSalt() {
		Random random = new Random();
		this.salt = random.ints(48, 123) //Alphanumeric ASCII chars
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) //Only alphanumeric, there are some symbols in between
				.limit(32)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint,  StringBuilder::append)
				.toString();
	}
	
	// ----------------------------------------------------------------------- //
	// Permission-related                                                      //
	// ----------------------------------------------------------------------- //
	
	public boolean canEdit(User user) {
		for(Group group : user.getGroups()) {
			if(!this.hasPermission("nightweb.admin.canEditGroups." + group.getId())) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean canEdit(Group group) {
		return this.hasPermission("nightweb.admin.canEditGroups." + group.getId());
	}
}
