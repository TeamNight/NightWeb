package dev.teamnight.nightweb.core.module;

import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Context;

public class ModuleContext implements Context {

	protected ApplicationContext getApplicationContext() {
		return null;
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
	public void registerServlet(Class<? extends HttpServlet> servlet, String pathInfo) {
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

}
