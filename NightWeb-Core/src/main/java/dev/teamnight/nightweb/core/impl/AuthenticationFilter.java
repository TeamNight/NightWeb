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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Jonas
 *
 */
public class AuthenticationFilter implements Filter {

	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.info("Starting filter " + filterConfig.getFilterName());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LOGGER.info("Testing out Filter");
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
