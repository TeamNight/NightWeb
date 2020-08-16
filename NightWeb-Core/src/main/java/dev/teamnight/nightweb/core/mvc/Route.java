/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jonas
 *
 */
public interface Route {
	
	public Optional<Controller> getController();
	
	public String getPathSpec();
	public Pattern getCompiledPathSpec();
	
	public String getHttpMethod();
	
	public String getAccepts();
	public String getProduces();
	
	public Result execute(HttpServletRequest request, HttpServletResponse response,
			Map<RequestParameter, String> parameters);
	
}
