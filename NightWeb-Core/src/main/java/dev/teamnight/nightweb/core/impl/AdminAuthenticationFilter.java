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

import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.entities.ApplicationData;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
public class AdminAuthenticationFilter implements Filter {

	private static final Logger LOGGER = LogManager.getLogger();
	private ApplicationData data;
	private Context ctx;
	
	public AdminAuthenticationFilter(ApplicationContext context, ApplicationData coreData) {
		if(coreData == null) {
			throw new IllegalArgumentException("coreData can not be null");
		}
		
		this.data = coreData;
		this.ctx = context;
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
			
			Authenticator auth = ctx.getAuthenticator(req.getSession());
			
			if(!auth.isAuthenticated() || req.getSession().getAttribute("dev.teamnight.nightweb.core.session.adminSession") == null) {
				resp.sendRedirect(StringUtil.filterURL(this.data.getContextPath() + "/admin/login"));
				return;
			}
			
			if(!auth.getUser().hasPermission("nightweb.admin.canUseACP")) {
				req.getSession().removeAttribute("dev.teamnight.nightweb.core.session.adminSession");
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
