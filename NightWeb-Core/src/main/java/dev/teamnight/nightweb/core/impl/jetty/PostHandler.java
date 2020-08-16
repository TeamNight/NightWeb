/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import dev.teamnight.nightweb.core.RequestContext;

/**
 * @author Jonas
 *
 */
public class PostHandler extends AbstractHandler {
	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		//Clear the thread-locals
		RequestContext.setModuleContext(null);
		RequestContext.setAuthenticator(null);
	}
	
}
