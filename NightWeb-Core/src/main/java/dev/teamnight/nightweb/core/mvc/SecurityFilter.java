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
	 * @param pathInfo
	 * @return
	 */
	public boolean matches(HttpServletRequest request, Collection<FilterEntry> entries);
	
}
