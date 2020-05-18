/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import dev.teamnight.nightweb.core.entities.User;

/**
 * An interface to provide applications with authentication mechanisms.
 * An implementation has to provide a constructor with a {@link dev.teamnight.nightweb.core.Context} Context and {@link javax.servlet.http.HttpSession} as parameter.
 * @author Jonas
 */
public interface Authenticator {

	/**
	 * Returns the context for which this authenticator works
	 * @return
	 */
	public Context getContext();
	
	/**
	 * Sets the context for the Authenticator
	 * @param context
	 */
	public void setContext(Context context);
	
	/**
	 * Gets the user involved with this Authenticator
	 * @return
	 */
	public User getUser();
	
	/**
	 * Sets the user
	 * @param user
	 */
	public void setUser(User user);
	
	/**
	 * Checks whether the user has two factor enabled and therefore needs another authentication
	 * @return
	 */
	public boolean isTwoFactorEnabled();
	
	/**
	 * Checks the password with the User object in order to provide authentication
	 * @param password
	 * @return
	 */
	public boolean authenticate(String password);
	
	/**
	 * Checks if the the two factor code
	 * @param twoFactorCode
	 * @return
	 */
	public boolean authenticate(int twoFactorCode);
	
	/**
	 * @return boolean true if the user is authenticated, otherwise false
	 */
	public boolean isAuthenticated();
	
	/**
	 * Invalidates the authentication and therefore logs the user out
	 */
	public void invalidate();
	
}
