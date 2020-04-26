package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpServlet;

import org.hibernate.Session;

public interface NightModule {
	
	public void init(Context ctx);
	
	public Context getContext();
	
	public void addServlet(HttpServlet servlet);
	
	public void addServlet(HttpServlet servlet, String pathInfo);
	
	public void addServlet(Class<? extends HttpServlet> servlet);
	
	public void addServlet(Class<? extends HttpServlet> servlet, String pathInfo);
	
	public Session getDatabase();
	
	public boolean isEnabled();
	
}
