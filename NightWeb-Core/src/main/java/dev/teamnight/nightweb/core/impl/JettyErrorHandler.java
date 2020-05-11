/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.handler.ErrorHandler;

import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.entities.ErrorLogEntry;
import dev.teamnight.nightweb.core.exceptions.TemplateProcessException;
import dev.teamnight.nightweb.core.service.ErrorLogService;
import dev.teamnight.nightweb.core.template.TemplateBuilder;
import dev.teamnight.nightweb.core.template.TemplateManager;

/**
 * @author Jonas
 *
 */
public class JettyErrorHandler extends ErrorHandler {

	private TemplateManager templateManager;
	
	public JettyErrorHandler(TemplateManager templateManager) {
		this.templateManager = templateManager;
	}

	@Override
	protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message,
			boolean showStacks) throws IOException {
		Throwable th = (Throwable)request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		this.writeErrorPageStacks(request, writer);
		
		ErrorLogEntry entry = new ErrorLogEntry();
		entry.setClassName(request.getRequestURI());
		entry.setErrorName(
				th != null ? th.getClass().getCanonicalName() : String.valueOf(code)
				);
		entry.setErrorMessage(th != null ? th.getMessage() : HttpStatus.getMessage(code));
		entry.setStackTrace(String.valueOf(request.getAttribute("stacktrace")));
		entry.setTime(new Date());
		
		ErrorLogService service = NightWeb.getServiceManager().getService(ErrorLogService.class);
		//TODO: remove comment for: service.save(entry);
		
		try {
			TemplateBuilder builder = this.templateManager.builder("errors/" + code + ".tpl");
			builder.assign("path", request.getRequestURI());
			
			if(NightWeb.getCoreApplication().isDebugModeEnabled() && showStacks) {
				builder.assign("stacktrace", request.getAttribute("stacktrace"));
			}
			
			String temp = builder.build();
			
			writer.write(temp);
			writer.flush();
		} catch(TemplateProcessException e) {
			writer.write(Integer.toString(code));
			writer.write(message);
			
			if(showStacks) {
				writer.write("Caused by: \n");
				writer.write((String) request.getAttribute("stacktrace"));
			}
			
			writer.flush();
		}
	}
	
	@Override
	protected void writeErrorPageStacks(HttpServletRequest request, Writer writer) throws IOException {
		Throwable th = (Throwable)request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		
		StringBuilder sb = new StringBuilder();
		
		while(th != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			th.printStackTrace(pw);
			
			sb.append(sw.toString());
			th = th.getCause();
		}
		
		request.setAttribute("stacktrace", sb.toString());
	}
	
}
