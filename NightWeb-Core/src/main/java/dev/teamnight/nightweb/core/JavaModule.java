package dev.teamnight.nightweb.core;

import dev.teamnight.nightweb.core.module.ModuleContext;

public abstract class JavaModule implements NightModule {

	// TODO Implement class
	
	private boolean enabled;
	private ModuleContext context;

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
	public boolean isEnabled() {
		return this.enabled;
	}
	
}
