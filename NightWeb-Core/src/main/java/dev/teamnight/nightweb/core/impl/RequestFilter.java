/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.entities.ApplicationData;
import dev.teamnight.nightweb.core.entities.SystemSetting;
import dev.teamnight.nightweb.core.service.SettingService;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
public class RequestFilter implements Filter {

	private static final Logger LOGGER = LogManager.getLogger();
	private ApplicationData data;
	private Context ctx;
	private SettingService setServ;
	
	private List<String> ignoreForceLoginPaths = new ArrayList<String>();

	
	public RequestFilter(ApplicationContext context, ApplicationData coreData) {
		if(coreData == null) {
			throw new IllegalArgumentException("coreData can not be null");
		}
		
		this.data = coreData;
		this.ctx = context;
		this.setServ = this.ctx.getServiceManager().getService(SettingService.class);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("Starting filter " + filterConfig.getFilterName());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LOGGER.info("RequestFilter is getting called");
		
		if(NightWeb.getCoreApplication().isDebugModeEnabled()) {
			this.ctx.getTemplateManager().clearTemplateCache();
		}
		
		request.setAttribute("context", this.ctx);
		
		if(request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			
			Authenticator auth = ctx.getAuthenticator(httpRequest.getSession());
			
			SystemSetting forceLogin = setServ.getByKey("forceLogin");
			if(forceLogin != null && forceLogin.getAsBoolean()) {
				for(String ignoreForceLoginPath : this.ignoreForceLoginPaths) {
					String regex = ignoreForceLoginPath.replace("*", "(.*)");
					LOGGER.debug("Regex for IgnoreForceLogin:" + ignoreForceLoginPath + ": is " + regex);
					
					if(httpRequest.getRequestURI().matches("^" + regex + "$")) {
						chain.doFilter(request, response);
					}
				}
				
				if(!auth.isAuthenticated()) {
					httpResponse.sendRedirect(StringUtil.filterURL(this.data.getContextPath() + "/login"));
					return;
				}
			}
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
	
	public void addIgnoreForceLoginPath(String path) {
		this.ignoreForceLoginPaths.add(path);
	}

}
