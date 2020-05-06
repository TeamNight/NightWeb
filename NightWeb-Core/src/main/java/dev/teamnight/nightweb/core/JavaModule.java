/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpServlet;

import org.hibernate.Session;

public abstract class JavaModule implements NightModule {
	
	private boolean enabled;
	private ModuleContext context;
	
	private String identifier;

	public abstract void init(ModuleContext ctx);
	
	public ModuleContext getModuleContext() {
		return this.context;
	}
	
	@Override
	public void init(Context ctx) {
		if(!(ctx instanceof ModuleContext)) {
			throw new IllegalArgumentException("JavaModule expects a ModuleContext");
		}
		
		this.context = (ModuleContext) ctx;
		
		this.init(this.context);
		this.enabled = true;
	}
	
	@Override
	public Context getContext() {
		return this.context;
	}

	@Override
	public void addServlet(Class<? extends HttpServlet> servlet) throws IllegalArgumentException {
		this.context.registerServlet(servlet);
	}

	@Override
	public void addServlet(Class<? extends HttpServlet> servlet, String pathSpec) {
		this.context.registerServlet(servlet, pathSpec);
	}

	@Override
	public Session getDatabase() {
		return this.context.getDatabaseSession();
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
