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
public interface PermissionOwner {
	
	public List<Permission> getPermissions();
	
	public List<Permission> getInheritedPermissions();

	public boolean hasPermission(Permission permission);
	
	public boolean hasPermission(String permission);
	
	public void addPermission(Permission permission);
	
	public void removePermission(Permission permission);
	
	public void setPermissions(List<Permission> permissions);
	
	public void clearPermissions();
	
}
