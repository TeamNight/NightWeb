/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpServlet;

import org.hibernate.Session;

import dev.teamnight.nightweb.core.impl.ModuleContext;

public abstract class JavaModule implements NightModule {

	// TODO Implement class
	
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
	public void addServlet(HttpServlet servlet) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addServlet(HttpServlet servlet, String pathSpec) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addServlet(Class<? extends HttpServlet> servlet) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addServlet(Class<? extends HttpServlet> servlet, String pathSpec) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Session getDatabase() {
		// TODO Auto-generated method stub
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
