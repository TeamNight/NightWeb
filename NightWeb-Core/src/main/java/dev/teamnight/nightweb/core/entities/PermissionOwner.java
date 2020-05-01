/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.entities;

import java.util.List;

/**
 * An interface for entities being able to own permissions by
 * owning by theirselves or inheriting them from another PermissionOwner
 * 
 * @author Jonas
 *
 */
public interface PermissionOwner<T extends Permission> {
	
	/**
	 * Returns a list of all direct permissions
	 * @return
	 */
	public List<T> getPermissions();
	
	/**
	 * Returns a list of all inherited permissions, e.g. Groups for Users
	 * @return
	 */
	public List<Permission> getInheritedPermissions();

	/**
	 * Returns whether the user has a direct or inherited permission that resolves to true
	 * @param Permission thePermission
	 * @return boolean
	 */
	public boolean hasPermission(Permission permission);
	
	/**
	 * Returns whether the user has a direct or inherited permission that resolves to true
	 * @param String permissionName
	 * @return boolean
	 */
	public boolean hasPermission(String permission);
	
	/**
	 * Adds a permission to the PermissionOwner
	 * @param permission
	 */
	public void addPermission(Permission permission);
	
	/**
	 * Removes a permission from the PermissionOwner
	 * @param permission
	 */
	public void removePermission(Permission permission);
	
	/**
	 * Sets the direct permission array
	 * @param permissions
	 */
	public void setPermissions(List<T> permissions);
	
	/**
	 * Clears all direct permissions
	 */
	public void clearPermissions();
	
}
