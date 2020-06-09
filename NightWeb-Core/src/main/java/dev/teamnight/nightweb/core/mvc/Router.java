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

	public void addController(Controller controller);
	
	public void addRoute(Method method, Controller controller);
	
	public List<MethodHolder> getRoutes();
	
	public String getContextPath();
	
	public String getPath(Controller controller);
	
	public String getPath(Controller controller, Method method);
	
	public String getPath(String controllerAndMethodName);
	
	public Controller getControllerByURL(String url);
	
	public MethodHolder getMethodByURL(String url);
	
	public Controller getController(String pathSpec);
	
	public MethodHolder getMethod(String pathSpec);
	
}
