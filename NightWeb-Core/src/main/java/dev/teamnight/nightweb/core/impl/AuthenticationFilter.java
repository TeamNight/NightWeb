/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
		LOGGER.info("-------------------------------------------------\n"
				+ "Testing for Request URI: " + request.getRequestURI()
				+ "\nMethod: " + request.getMethod()
				+ "\nContent-Type: " + (request.getContentType() == null ? "null" : request.getContentType())
				+ "\nAccept: " + (request.getHeader("Accept") == null ? "*/*" : request.getHeader("Accept"))
				+ "\n-------------------------------------------------");
		for(FilterEntry entry : entries) {
			LOGGER.info("-----------------------------------------------");
			LOGGER.info("Pattern: \"" + entry.getRegex().pattern() + "\"");
			LOGGER.info("Method: " + String.join(", ", entry.getHttpMethods()));
			LOGGER.info("Accept: " + String.join(", ", entry.getProduces()));
			LOGGER.info("Allowed Content-Types: " + String.join(", ", entry.getAccepts()));
			
			boolean uriMatches = false;
			boolean methodMatches = false;
			boolean producesMatches = false;
			boolean acceptsMatches = false;
			
			Matcher matcher = entry.getRegex().matcher(request.getRequestURI());
			
			//First, match the uri with the regex, if not, continue
			if(matcher.matches()) {
				uriMatches = true;
			} else {
				continue;
			}
			
			LOGGER.info("Request URI matches REGEX");
			
			//Then compare the methods of boths
			for(String method : entry.getHttpMethods()) {
				if(method.equalsIgnoreCase(request.getMethod())) {
					methodMatches = true;
				}
			}
			
			//If not, continue
			if(!methodMatches) {
				continue;
			}
			
			LOGGER.info("Method matching");
			
			//Then the Accept Header needs to be checked
			List<String> acceptHeaders = request.getHeader("Accept") == null ? Arrays.asList("text/html") : StringUtil.parseAcceptHeader(request.getHeader("Accept"));
			
			LOGGER.info("Accept-Header: " + String.join(" - ", acceptHeaders));
			
			//Now, compare both accept lists
			for(String produce : entry.getProduces()) {
				for(String requestProduce : acceptHeaders) {
					if(produce.equalsIgnoreCase(requestProduce)) {
						producesMatches = true;
					}
				}
			}
			
			//If no match, then continue
			if(!producesMatches) {
				continue;
			}
			
			LOGGER.info("Accept-Headers match");
			
			//Now the contentType needs to be checked
			String contentType = request.getContentType();
			
			if(contentType == null && entry.getAccepts().isEmpty()) {
				acceptsMatches = true;
			}
			
			if(!acceptsMatches) {
				if(contentType == null) {
					continue;
				}
				
				if(entry.getAccepts().isEmpty()) {
					continue;
				}
				
				for(String accept : entry.getAccepts()) {
					if(accept.equalsIgnoreCase(contentType)) {
						acceptsMatches = true;
					}
				}
			}
			
			if(uriMatches && methodMatches && producesMatches && acceptsMatches) {
				LOGGER.info("All matches");
				return true;
			}
		}
		return false;
	}
	
	private boolean containsEntry(FilterEntry entry) {
		return this.patterns.stream().filter(e -> e.getRegex().pattern().equalsIgnoreCase(entry.getRegex().pattern())).findAny().orElse(null) != null;
	}

}
