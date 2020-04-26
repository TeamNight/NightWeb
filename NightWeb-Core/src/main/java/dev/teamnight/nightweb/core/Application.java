package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpServlet;

import org.hibernate.Session;

public abstract class Application implements NightModule {

	// TODO Implement class
	
	private ApplicationContext context;

	public abstract void init(ApplicationContext ctx);
	
	@Override
	public void init(Context ctx) {
		if(!(ctx instanceof ApplicationContext)) {
			throw new IllegalArgumentException("Application expects an ApplicationContext");
		}
		
		this.context = (ApplicationContext) ctx;
		
		this.init(this.context);
	}

	@Override
	public Context getContext() {
		return this.context;
	}

	@Override
	public void addServlet(HttpServlet servlet) {
	}

	@Override
	public void addServlet(HttpServlet servlet, String pathInfo) {
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

}
