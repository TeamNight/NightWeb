/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "menu_items")
public class MenuItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private int id;
	
	@Column(nullable = false)
	private String identifier;
	
	@Column
	private String title;
	
	@Column
	private String servletClass;
	
	@Column
	private String customURL;
	
	@Column(nullable = false)
	private int order;
	
	@Column(nullable = false)
	private boolean disabled;
	
	@Column
	private String Permission;
	
	@ManyToOne
	@JoinColumn(name = "menuId", nullable = false)
	private Menu menu;
	
}
