/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpServlet;

import org.hibernate.Session;

public abstract class Application implements NightModule {

	// TODO Implement class
	
	private ApplicationContext context;
	private boolean enabled;
	
	private String identifier;

	public abstract void start(ApplicationContext ctx);
	
	@Override
	public void init(Context ctx) {
		if(!(ctx instanceof ApplicationContext)) {
			throw new IllegalArgumentException("Application expects an ApplicationContext");
		}
		
		this.context = (ApplicationContext) ctx;
		
		this.start(this.context);
		this.enabled = true;
	}

	@Override
	public Context getContext() {
		return this.context;
	}

	@Override
	public void addServlet(Class<? extends HttpServlet> servlet) {
	}

	@Override
	public void addServlet(Class<? extends HttpServlet> servlet, String pathInfo) {
	}

	@Override
	public Session getDatabase() {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	@Override
	public String getIdentifier() {
		return this.identifier;
	}
	
	@Override
	public void setIdentifier(String identifier) {
		if(this.identifier == null) {
			this.identifier = identifier;
		}
	}

}
