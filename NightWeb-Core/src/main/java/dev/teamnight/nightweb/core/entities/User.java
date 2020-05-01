/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import java.util.Date;
import java.util.List;

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
public class User {

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
}
