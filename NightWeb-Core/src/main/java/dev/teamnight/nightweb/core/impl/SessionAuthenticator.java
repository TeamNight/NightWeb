/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.util.Date;

import javax.servlet.http.HttpSession;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.UserService;

/**
 * An implementation of the Authenticator using the HttpSession as data store
 * @author Jonas
 */
public class SessionAuthenticator implements Authenticator {

	private Context ctx;
	private HttpSession session;
	
	private User user;
	private boolean authenticated = false;
	private boolean twoFactorAuthenticated = true;

	public SessionAuthenticator(Context ctx, HttpSession session) {
		this.ctx = ctx;
		this.session = session;
		
		long userId = 0L;
		
		if(session.getAttribute("dev.teamnight.nightweb.core.session.userId") != null) {
			userId = (long) session.getAttribute("dev.teamnight.nightweb.core.session.userId");
		}
		
		if(userId != 0L) {
			this.user = ctx.getServiceManager().getService(UserService.class).getOne(userId);
			this.authenticated = true;
		}
	}
	
	@Override
	public User getUser() {
		return this.user;
	}

	@Override
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean authenticate(String password) {
		if(user == null) {
			return false;
		}
		
		if(user.isDisabled()) {
			this.authenticated = false;
			return false;
		}
		
		if(user.isBanned()) {
			this.authenticated = false;
			return false;
		}
		
		if(user.getPassword().equals(user.createHash(password))) {
			this.authenticated = true;
		}
		
		this.session.setAttribute("dev.teamnight.nightweb.core.session.userId", this.user.getId());
		
		UserService userv = ctx.getServiceManager().getService(UserService.class);
		
		user.setLastLoginDate(new Date());
		userv.save(user);
		
		return authenticated;
	}

	@Override
	public boolean authenticate(int twoFactorCode) {
		return false;
	}

	@Override
	public boolean isTwoFactorEnabled() {
		return false;
	}

	@Override
	public boolean isAuthenticated() {
		return this.user != null && this.authenticated && this.twoFactorAuthenticated;
	}

	@Override
	public void invalidate() {
		this.authenticated = false;
		this.twoFactorAuthenticated = false;
		this.user = null;
		this.session.removeAttribute("dev.teamnight.nightweb.core.session.userId");
		this.session.removeAttribute("dev.teamnight.nightweb.core.session.adminSession");
		this.session = null;
	}

	@Override
	public Context getContext() {
		return this.ctx;
	}

	@Override
	public void setContext(Context context) {
		this.ctx = context;
	}

}
