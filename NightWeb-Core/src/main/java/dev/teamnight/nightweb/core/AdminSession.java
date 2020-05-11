/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

/**
 * @author Jonas
 *
 */
public class AdminSession extends WebSession {
	
	/**
	 * @param ctx
	 */
	public AdminSession(Context ctx) {
		super(ctx);
	}
	
	/**
	 * Copies the data of an already logged in session to this session
	 * @param session
	 */
	public AdminSession(WebSession session) {
		this(session.getContext());
		this.setUser(session.getUser());
	}

}
