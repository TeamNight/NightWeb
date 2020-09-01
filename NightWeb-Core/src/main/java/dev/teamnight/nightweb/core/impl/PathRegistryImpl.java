/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import dev.teamnight.nightweb.core.PathRegistry;
import dev.teamnight.nightweb.core.mvc.Route;
import dev.teamnight.nightweb.core.mvc.Router;

/**
 * @author Jonas
 *
 */
public class PathRegistryImpl implements PathRegistry {

	@Override
	public Map<String, Filter> getMappedFilters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Servlet> getMappedServlets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Route> getRoutes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Filter> getFilters(String pathSpec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servlet getServlet(String pathSpec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Route getRoute(String pathSpec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Filter getFilterByClassName(String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servlet getServletByClassName(String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Route getRouteBySignature(String classOrMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Filter> matchFilter(String uri, boolean withContextPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servlet matchServlet(String uri, boolean withContextPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Route matchRoute(String uri, boolean withContextPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addFilter(String pathSpec, Filter filter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addServlet(String pathSpec, Servlet servlet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addRouter(Router router) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addRoute(String pathSpec, Route route) {
		// TODO Auto-generated method stub
		
	}

}
