/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.Source;

import dev.teamnight.nightweb.core.Context;

/**
 * @author Jonas
 * @deprecated will be removed in version 0.3
 */
@Deprecated
public class NightJettyServletHolder extends ServletHolder {

	private Context ctx;
	
	public NightJettyServletHolder(Class<? extends Servlet> servlet) {
		super(Source.EMBEDDED);
		this.setHeldClass(servlet);
	}
	
	public NightJettyServletHolder setContext(Context context) {
		this.ctx = context;
		
		return this;
	}
	
	public Context getContext() {
		return this.ctx;
	}
	
	@Override
	public void handle(Request baseRequest, ServletRequest request, ServletResponse response)
			throws ServletException, UnavailableException, IOException {
		super.handle(baseRequest, request, response);
	}
}
