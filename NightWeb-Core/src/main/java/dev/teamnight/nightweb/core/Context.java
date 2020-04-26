package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

public interface Context {

	public void registerServlet(HttpServlet servlet);
	
	public void registerServlet(HttpServlet servlet, String pathInfo);
	
	public void registerServlet(Class<? extends HttpServlet> servlet);
	
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathInfo);
	
	public Session getDatabaseSession();
	
	public Logger getLogger();
	
	public Logger getLogger(Class<?> object);
	
	public String getContextPath();
	
}
