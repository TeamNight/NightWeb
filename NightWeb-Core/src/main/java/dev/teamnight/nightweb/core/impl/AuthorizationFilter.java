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
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.mvc.FilterEntry;
import dev.teamnight.nightweb.core.mvc.SecurityFilter;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
public class AuthorizationFilter implements SecurityFilter {

	private static final Logger LOGGER = LogManager.getLogger();
	private Context ctx;
	
	private List<FilterEntry> patterns = new ArrayList<FilterEntry>();
	
	public AuthorizationFilter(ApplicationContext context) {
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
			
			User user = null;
			
			Authenticator auth = ctx.getAuthenticator(req.getSession());
			
			if(auth.isAuthenticated()) {
				user = auth.getUser();
			} else {
				user = new User("guest", "guest"); //TODO: Replace with default user
			}
			
			FilterEntry entry = this.getEntry(req);
			if(entry == null) {
				this.ctx.getTemplate("permissionError.tpl").send(resp);
				return;
			}
			
			String permission = (String) entry.getAttributes().get("dev.teamnight.nightweb.core.permission");
			
			if(!user.hasPermission(permission)) {
				if(permission.startsWith("nightweb.admin")) {
					this.ctx.getTemplate("admin/permissionError.tpl").send(resp);
				} else {
					this.ctx.getTemplate("permissionError.tpl").send(resp);
				}
				return;
			}
			
			chain.doFilter(request, response);
		} else {
			throw new ServletException("AuthorizationFilter only allows HTTP Requests");
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
		if(entry.getAttributes().get("dev.teamnight.nightweb.core.permission") == null) {
			throw new IllegalArgumentException("Attribute \"dev.teamnight.nightweb.core.permission\" can not be null");
		}
		
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
			
			if(entry.getAccepts().isEmpty()) {
				acceptsMatches = true;
			}
			
			if(!acceptsMatches) {
				if(contentType == null) {
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
	
	public FilterEntry getEntry(HttpServletRequest request) {
		return this.patterns.stream()
				.filter(entry -> entry.getRegex().matcher(request.getRequestURI()).matches())
				.filter(entry -> {
					for(String method : entry.getHttpMethods()) {
						if(method.equalsIgnoreCase(request.getMethod())) {
							return true;
						}
					}
					return false;
				})
				.filter(entry -> {
					List<String> acceptHeaders = request.getHeader("Accept") == null ? Arrays.asList("text/html") : StringUtil.parseAcceptHeader(request.getHeader("Accept"));
					
					for(String produce : entry.getProduces()) {
						for(String requestProduce : acceptHeaders) {
							if(produce.equalsIgnoreCase(requestProduce)) {
								return true;
							}
						}
					}
					
					return false;
				})
				.filter(entry -> {
					if(entry.getAccepts().isEmpty()) {
						return true;
					}
					
					String contentType = request.getContentType();
					
					if(contentType == null) {
						return false;
					}
					
					for(String accept : entry.getAccepts()) {
						if(accept.equalsIgnoreCase(contentType)) {
							return true;
						}
					}
					return false;
				})
				.findFirst()
				.orElse(null);
	}
	
	private boolean containsEntry(FilterEntry entry) {
		return this.patterns.stream().filter(e -> e.getRegex().pattern().equalsIgnoreCase(entry.getRegex().pattern())).findAny().orElse(null) != null;
	}

}
