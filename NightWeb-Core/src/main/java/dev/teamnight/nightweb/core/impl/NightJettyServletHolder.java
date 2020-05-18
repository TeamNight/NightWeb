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
import dev.teamnight.nightweb.core.NightWeb;

/**
 * @author Jonas
 *
 */
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
		if(this.ctx == null) {
			throw new IllegalStateException("Context was not set");
		}
		
		if(NightWeb.getCoreApplication().isDebugModeEnabled()) {
			this.ctx.getTemplateManager().clearTemplateCache();
		}
		
		request.setAttribute("context", this.ctx);
		
		super.handle(baseRequest, request, response);
	}
}
