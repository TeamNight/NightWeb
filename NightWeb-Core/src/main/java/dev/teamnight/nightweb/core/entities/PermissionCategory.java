/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Jonas
 */
@Entity
@Table(name = "permission_categories")
public class PermissionCategory implements Comparable<PermissionCategory> {

	@Id
	@GeneratedValue
	@Column(nullable = false)
	private int id;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@Column
	private int showOrder = 0;
	
	@ManyToOne
	@JoinColumn(name = "parentId")
	private PermissionCategory parent;
	
	protected PermissionCategory() {}
	
	/**
	 * @param name
	 * @param order
	 * @param parent
	 */
	public PermissionCategory(String name, int order, PermissionCategory parent) {
		this.name = name;
		this.showOrder = order;
		this.parent = parent;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the order
	 */
	public int getOrder() {
		return showOrder;
	}
	
	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.showOrder = order;
	}
	
	/**
	 * @return the parent
	 */
	public PermissionCategory getParent() {
		return parent;
	}
	
	/**
	 * @param parent the parent to set
	 */
	public void setParent(PermissionCategory parent) {
		this.parent = parent;
	}

	@Override
	public int compareTo(PermissionCategory o) {
		//TODO this needs to be implemented by the following, a category that has less parents is lower.
		// If the parent categories are the same, only the order integer counts. If they aren't the same,
		// but both have equal count of parents, then the one with the lower parent becomes lower.
		// e.g. admin.general wins over moderator.general
		
		return Integer.compare(this.showOrder, o.showOrder);
	}
}
