/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

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
import dev.teamnight.nightweb.core.mvc.FilterEntry;
import dev.teamnight.nightweb.core.mvc.SecurityFilter;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
public class AuthenticationFilter implements SecurityFilter {

	private static final Logger LOGGER = LogManager.getLogger();
	private ApplicationData data;
	private Context ctx;
	
	private List<FilterEntry> patterns = new ArrayList<FilterEntry>();
	
	public AuthenticationFilter(ApplicationContext context, ApplicationData coreData) {
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
			
			LOGGER.info("Doing auth -> RequestURI: " + req.getRequestURI());
			LOGGER.info("Doing auth -> matches: " + this.matches(req, this.patterns));
			
			if(!this.matches(req, this.patterns)) {
				chain.doFilter(request, response);
				return;
			}
			
			Authenticator auth = ctx.getAuthenticator(req.getSession());
			
			if(!auth.isAuthenticated()) {
				resp.sendRedirect(StringUtil.filterURL(this.data.getContextPath() + "/login"));
				return;
			}
			
			chain.doFilter(request, response);
		} else {
			throw new ServletException("AuthenticationFilter only allows HTTP Requests");
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public List<FilterEntry> getPatterns() {
		return Collections.unmodifiableList(this.patterns);
	}

	@Override
	public void addPattern(FilterEntry entry) {
		if(!this.containsEntry(entry)) {
			this.patterns.add(entry);
		} else {
			for(FilterEntry e : this.patterns) {
				if(e.getRegex().pattern().equalsIgnoreCase(entry.getRegex().pattern())) {
					entry.getHttpMethods().forEach(method -> e.addMethod(method));
					entry.getProduces().forEach(produces -> e.addProduces(produces));
					entry.getAccepts().forEach(accepts -> e.addAccepts(accepts));
				}
			}
		}
	}


	@Override
	public void removePattern(FilterEntry entry) {
		this.patterns.remove(entry);
	}

	@Override
	public boolean matches(HttpServletRequest request, Collection<FilterEntry> entries) {
		for(FilterEntry entry : entries) {
			LOGGER.info("Doing auth -> Pattern: \"" + entry.getRegex().pattern() + "\"");
			
			boolean matches = false;
			
			Matcher matcher = entry.getRegex().matcher(request.getRequestURI());
			
			//First, match the uri with the regex, if not, continue
			if(matcher.matches()) {
				matches = true;
			} else {
				continue;
			}
			
			//Then compare the methods of boths
			for(String method : entry.getHttpMethods()) {
				if(method.equalsIgnoreCase(request.getMethod())) {
					matches = true;
				}
			}
			
			//If not, continue
			if(!matches) {
				continue;
			}
			
			//Then the Accept Header needs to be checked
			List<String> accepts = StringUtil.parseAcceptHeader(request.getHeader("Accept") == null ? "" : request.getHeader("Accept"));
			
			//Now, compare both accept lists
			if(accepts.isEmpty()) {
				if(entry.getProduces().isEmpty()) {
					matches = true;
				} else {
					continue;
				}
			} else {
				if(entry.getProduces().isEmpty()) {
					matches = true;
				} else {
					for(String produce : entry.getProduces()) {
						for(String value : accepts) {
							if(produce.equalsIgnoreCase(value)) {
								matches = true;
							}
 						}
					}
				}
			}
			
			//If no match, then continue
			if(!matches) {
				continue;
			}
			
			//Now the contentType needs to be checked
			String contentType = request.getContentType();
			
			if(contentType == null && !entry.getAccepts().isEmpty()) {
				continue;
			}
			
			if(entry.getAccepts().isEmpty()) {
				matches = true;
			} else {
				for(String accept : entry.getAccepts()) {
					if(accept.equalsIgnoreCase(contentType)) {
						matches = true;
					}
				}
			}
			
			if(matches) {
				return true;
			}
		}
		return false;
	}
	
	private boolean containsEntry(FilterEntry entry) {
		return this.patterns.stream().filter(e -> e.getRegex().pattern().equalsIgnoreCase(entry.getRegex().pattern())).findAny().orElse(null) != null;
	}

}
