/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "permissions")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Permission {

	@Id
	@GeneratedValue
	private long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private Permission.Type type;
	
	@Column(nullable = false)
	private String value;
	
	/**
		 * @author Jonas
		 *
		 */
	public enum Type {
		STRING,
		NUMBER,
		FLAG
	}
}
