/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.Collection;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Jonas
 *
 */
public interface SecurityFilter extends Filter {

	public List<FilterEntry> getPatterns();
	
	public void addPattern(FilterEntry entry);
	
	public void removePattern(FilterEntry entry);
	
	/**
	 * <p>The matches method matches the requestURI, this includes
	 * the contextPath and the pathSpec, any class registering
	 * a pattern should include both.<p>
	 * 
	 * <p>The matching should be done the following way in order
	 * to prevent inconsistencies between the methods:
	 * 
	 * 1. RequestURI should match, if not, return false
	 * 2. Method should match, if not, return false
	 * 3. Produces ("accept"-Header) gets "text/html" if it is null
	 *    or empty. Then, request and entry must have the same value.
	 * 4. Accepts ("Content-Type"-Header) gets null as default.
	 *    If request and entry accepts are null, then return true.
	 *    If only one of request or entry is null, return false.
	 *    If both are not null, both must have the same value for true.
	 * @param pathInfo
	 * @return
	 */
	public boolean matches(HttpServletRequest request, Collection<FilterEntry> entries);
	
}
