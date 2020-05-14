/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;

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

import dev.teamnight.nightweb.core.AdminSession;
import dev.teamnight.nightweb.core.StringUtil;
import dev.teamnight.nightweb.core.WebSession;
import dev.teamnight.nightweb.core.entities.ApplicationData;

/**
 * @author Jonas
 *
 */
public class AdminAuthenticationFilter implements Filter {

	private static final Logger LOGGER = LogManager.getLogger();
	private ApplicationData data;
	
	public AdminAuthenticationFilter(ApplicationData coreData) {
		if(coreData == null) {
			throw new IllegalArgumentException("coreData can not be null");
		}
		
		this.data = coreData;
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("Starting filter " + filterConfig.getFilterName());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if(request instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse resp = (HttpServletResponse) response;
			
			AdminSession session = WebSession.getSession(req, AdminSession.class);
			
			if(session == null || !session.isLoggedIn()) {
				resp.sendRedirect(StringUtil.filterURL(this.data.getContextPath() + "/admin/login"));
				return;
			}
			
			chain.doFilter(request, response);
		} else {
			response.getWriter().write("401 - Unauthorized");
		}
	}

	@Override
	public void destroy() {
	}

}
