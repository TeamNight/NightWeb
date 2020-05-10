/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "user_settings", uniqueConstraints = @UniqueConstraint(columnNames = {"settingKey", "user"}))
public class UserSetting extends Setting {

	@ManyToOne
	@Column(nullable = false)
	private User user;
	
	public UserSetting(Setting baseSetting) {
		super(baseSetting);
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
}
