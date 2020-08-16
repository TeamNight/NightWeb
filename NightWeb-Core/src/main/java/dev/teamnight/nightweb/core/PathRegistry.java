/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import dev.teamnight.nightweb.core.mvc.Route;
import dev.teamnight.nightweb.core.mvc.Router;

/**
 * An interface to provide accessibility to all registered
 * servlets, filters or routes using the pathSpec, a given
 * request uri or the class (& method) name.
 * 
 * @author Jonas
 */
public interface PathRegistry {

	/**
	 * Map<String, Filter> pathToFilter
	 * Map<String, Servlet> pathToServlet
	 * Map<String, Route> pathToRoute
	 */
	
	/**
	 * A map of all filters that were registered using a ServletRegistrationAdapter.
	 * 
	 * @return Map<String, Filter>
	 */
	Map<String, Filter> getMappedFilters();
	
	/**
	 * A map of all servlets that were registered using a ServletRegistrationAdapter.
	 * 
	 * @return Map<String, HttpServlet>
	 */
	Map<String, Servlet> getMappedServlets();
	
	/**
	 * A map of all routes that were registered using a Router.
	 * 
	 * @return Map<String, Route>
	 */
	Map<String, Route> getRoutes();
	
	/**
	 * Retrieves a filter from the map of filters using its pathSpec.
	 * 
	 * @param String the pathSpec
	 * @return List of {@link javax.servlet.Filter} the filter
	 */
	public List<Filter> getFilters(String pathSpec);
	
	/**
	 * Retrieves a HttpServlet from the map of servlets using its pathSpec.
	 * 
	 * @param String the pathSpec
	 * @return {@link javax.servlet.http.HttpServlet} the servlet or {@code null}
	 */
	public Servlet getServlet(String pathSpec);
	
	/**
	 * Retrieves a route from the map of routes using its pathSpec.
	 * 
	 * @param String the pathSpec
	 * @return {@link dev.teamnight.nightweb.core.mvc.Route} the route or {@code null}
	 */
	public Route getRoute(String pathSpec);
	
	/**
	 * Retrieves a filter from the map of filters using its class name.
	 * 
	 * @param String the class name
	 * @return {@link javax.servlet.Filter} the filter or {@code null}
	 */
	public Filter getFilterByClassName(String className);
	
	/**
	 * Retrieves a HttpServlet from the map of servlets using its class name.
	 * 
	 * @param String the class name
	 * @return {@link javax.servlet.http.HttpServlet} the servlet or {@code null}
	 */
	public Servlet getServletByClassName(String className);
	
	/**
	 * Retrieves a route from the map of routes using its class name or method name.
	 * 
	 * @param String the class or method name
	 * @return {@link dev.teamnight.nightweb.core.mvc.Route} the route or {@code null}
	 */
	public Route getRouteBySignature(String classOrMethod);
	
	/**
	 * Matches a filter using a given request uri.
	 * 
	 * @param String the URI
	 * @return List of {@link javax.servlet.Filter} the filter
	 */
	public List<Filter> matchFilter(String uri);
	
	/**
	 * Matches a servlet using a given request uri.
	 * 
	 * @param String the URI
	 * @return {@link javax.servlet.http.HttpServlet} the servlet or {@code null}
	 */
	public Servlet matchServlet(String uri);
	
	/**
	 * Matches a route using a given request uri.
	 * 
	 * @param String the URI
	 * @return {@link dev.teamnight.nightweb.core.mvc.Route} the route or {@code null}
	 */
	public Route matchRoute(String uri);
	
	/**
	 * Adds a filter to the path registry.
	 * 
	 * @param String the pathSpec
	 * @param {@link javax.servlet.Filter} the filter to register
	 */
	public void addFilter(String pathSpec, Filter filter);
	
	/**
	 * Adds a servlet to the path registry.
	 * 
	 * @param String the pathSpec
	 * @param {@link javax.servlet.http.HttpServlet} the servlet to register
	 */
	public void addServlet(String pathSpec, Servlet servlet);
	
	/**
	 * Adds a router to match routes by class or method signature.
	 * 
	 * @param {@link dev.teamnight.nightweb.core.mvc.Router} the router
	 */
	public void addRouter(Router router);
	
	/**
	 * Adds a route to the path registry.
	 * 
	 * @param String the pathSpec
	 * @param {@link dev.teamnight.nightweb.core.mvc.Route} the route to register
	 */
	public void addRoute(String pathSpec, Route route);
}
