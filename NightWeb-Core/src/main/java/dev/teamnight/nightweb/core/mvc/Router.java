/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Jonas
 *
 */
public interface Router {

	/**
	 * Adds a controller to the list of controllers
	 * and adds all method annotated with {@link Path}
	 * to the routes.
	 * 
	 * @param {@link Controller} the controller
	 */
	public void addController(Controller controller);
	
	/**
	 * Adds a specific method of a controller to the
	 * list of routes.
	 * 
	 * @param {@link Method} the method
	 * @param {@link Controller} the methods controller
	 */
	public void addRoute(Method method, Controller controller);
	
	/**
	 * An unmodifiable list of all registered routes
	 * @return List<MethodHolder> the holders
	 */
	public List<MethodHolder> getRoutes();
	
	/**
	 * The context path of this router
	 * @return String the contextPath
	 */
	public String getContextPath();
	
	/**
	 * Returns the path resolver that compiles the
	 * pathSpecs and resolves path parameters
	 * 
	 * @return {@link PathResolver} the pathResolver
	 */
	public PathResolver getPathResolver();
	
	/**
	 * Returns the path of the controller if it
	 * is annotated with {@link Path} with the
	 * contextPath preprended.
	 * 
	 * @param {@link Controller} the controller
	 * @return String the path
	 */
	public String getPath(Controller controller);
	
	/**
	 * Returns the path of the method of the
	 * controller if one or both are annotated
	 * with {@link Path} with the contextPath
	 * prepended.
	 * 
	 * @param {@link Controller} the controller
	 * @param {@link java.lang.Method} the Method
	 * @return String the path
	 */
	public String getPath(Controller controller, Method method);
	
	/**
	 * Returns the path for the method specified
	 * by the canoncial name of the Controller and
	 * the name of the Method.
	 * 
	 * <p>This requires a method to have a standalone name</p>
	 * @param {@link String} the name of the controller and method as "package.Controller.method"
	 * @return String the path
	 */
	public String getPath(String controllerAndMethodName);
	
	/**
	 * Returns the controller matching the url without
	 * the context path.
	 * 
	 * @param String the url
	 * @return {@link Controller} the according controller
	 */
	public Controller getControllerByURL(String url);
	
	
	/**
	 * Returns the MethodHolders matching the url
	 * 
	 * @param {@link RouteQuery} a query containing information
	 * @return {@link MethodHolder}[] the method holder
	 */
	public MethodHolder getMethodByURL(RouteQuery query);
	
	/**
	 * Returns a controller by its pathSpec, defined
	 * in a {@link Path} annotation.
	 * 
	 * @param pathSpec
	 * @return {@link Controller} or {@code null} if no controller is found
	 */
	public Controller getController(String pathSpec);
	
	/**
	 * Returns the method holder by its pathSpec,
	 * defined in the {@link Path} annotation.
	 * 
	 * @param {@link RouteQuery} a query containing information
	 * @return {@link Controller} or {@code null} if no method holder is found
	 */
	public MethodHolder getMethod(RouteQuery query);
	
}
