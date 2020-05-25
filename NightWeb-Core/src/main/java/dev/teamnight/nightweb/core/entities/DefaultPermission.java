/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Jonas
 *
 */
@Entity
@Table(name = "permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class DefaultPermission extends Permission implements Comparable<DefaultPermission> {
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "categoryId")
	public PermissionCategory category;
	
	@Column
	public int showOrder = 0;
	
	protected DefaultPermission() {}
	
	// ----------------------------------------------------------------------- //
	// Constructor ignoring category and showOrder                             //
	// ----------------------------------------------------------------------- //
	
	public DefaultPermission(String name, short value, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), null, 0, data);
	}
	
	public DefaultPermission(String name, int value, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), null, 0, data);
	}
	
	public DefaultPermission(String name, long value, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), null, 0, data);
	}
	
	public DefaultPermission(String name, double value, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), null, 0, data);
	}
	
	public DefaultPermission(String name, Tribool value, ModuleData data) {
		this(name, Type.FLAG, value.getAsString(), null, 0, data);
	}
	
	// ----------------------------------------------------------------------- //
	// Constructor for Category and showOrder                                  //
	// ----------------------------------------------------------------------- //
	
	public DefaultPermission(String name, short value, PermissionCategory category, int showOrder, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), category, showOrder, data);
	}
	
	public DefaultPermission(String name, int value, PermissionCategory category, int showOrder, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), category, showOrder, data);
	}
	
	public DefaultPermission(String name, long value, PermissionCategory category, int showOrder, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), category, showOrder, data);
	}
	
	public DefaultPermission(String name, double value, PermissionCategory category, int showOrder, ModuleData data) {
		this(name, Type.NUMBER, String.valueOf(value), category, showOrder, data);
	}
	
	public DefaultPermission(String name, Tribool value, PermissionCategory category, int showOrder, ModuleData data) {
		this(name, Type.FLAG, value.getAsString(), category, showOrder, data);
	}
	
	// ----------------------------------------------------------------------- //
	// Base Constructor                                                        //
	// ----------------------------------------------------------------------- //
	
	public DefaultPermission(String name, Permission.Type type, String value, PermissionCategory category, int showOrder, ModuleData data) {
		super(name, Type.FLAG, value, data);
		this.category = category;
		this.showOrder = showOrder;
	}
	
	/**
	 * @return the category
	 */
	public PermissionCategory getCategory() {
		return category;
	}
	
	/**
	 * @param category the category to set
	 */
	public void setCategory(PermissionCategory category) {
		this.category = category;
	}
	
	/**
	 * @return the showOrder
	 */
	public int getShowOrder() {
		return showOrder;
	}
	
	/**
	 * @param showOrder the showOrder to set
	 */
	public void setShowOrder(int showOrder) {
		this.showOrder = showOrder;
	}

	@Override
	public int compareTo(DefaultPermission o) {
		int categoryCompare = this.category.compareTo(o.category);
		
		if(categoryCompare == 0) {
			return Integer.compare(this.showOrder, o.showOrder);
		}
		
		return categoryCompare;
	}
}
