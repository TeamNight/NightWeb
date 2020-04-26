package dev.teamnight.nightweb.core.module;

import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.ApplicationContext;

public class JettyApplicationContext implements ApplicationContext {

	private ServletContextHandler handler;
	
	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathSpec) {
		this.handler.addServlet(servlet, pathSpec);
	}

	@Override
	public void registerServlet(HttpServlet servlet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerServlet(HttpServlet servlet, String pathInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerServlet(Class<? extends HttpServlet> servlet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Session getDatabaseSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Logger getLogger(Class<?> object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SessionFactory getSessionFactory() {
		// TODO Auto-generated method stub
		return null;
	}

}
