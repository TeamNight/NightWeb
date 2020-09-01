/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.InvokedRoute;
import dev.teamnight.nightweb.core.mvc.RequestParameter;
import dev.teamnight.nightweb.core.mvc.Result;

/**
 * @author Jonas
 *
 */
public class MethodHolder implements InvokedRoute {

	private Method invokeMethod;
	private Controller controller;
	private Context ctx;
	
	private String pathSpec;
	private Pattern regex;
	private String httpMethod;
	
	private String accepts;
	private String produces;
	
	private Map<RequestParameter, Integer> parameters = new HashMap<RequestParameter, Integer>();
	private int posReq = -1;
	private int posRes = -1;
	private int posCtx = -1;
	private int posParamMap = -1;
	
	/**
	 * @param ctx
	 * @param invokeMethod
	 * @param controller
	 */
	public MethodHolder(Context ctx, Method invokeMethod, Controller controller) {
		this.ctx = ctx;
		this.invokeMethod = invokeMethod;
		this.controller = controller;
		this.produces = "text/html";
	}
	
	@Override
	public Result execute(HttpServletRequest req, HttpServletResponse res, Map<RequestParameter, String> parameters) throws ServletException {
		int paramsSize = parameters.size();
		
		if(this.posReq > -1) {
			paramsSize++;
		}
		
		if(this.posRes > -1) {
			paramsSize++;
		}
		
		if(this.posCtx > -1) {
			paramsSize++;
		}
		
		if(this.posParamMap > -1) {
			paramsSize++;
		}
		
		Object[] args = new Object[paramsSize];
		
		if(this.posReq > -1) args[this.posReq] = req;
		if(this.posRes > -1) args[this.posRes] = res;
		if(this.posCtx > -1) args[this.posCtx] = this.ctx;
		if(this.posParamMap > -1) args[this.posParamMap] = parameters;
		
		for(Entry<RequestParameter, String> entry : parameters.entrySet()) {
			if(!this.parameters.containsKey(entry.getKey())) {
				throw new IllegalArgumentException("parameter " + entry.getKey() + " not found in method signature");
			}
			
			int pos = this.parameters.get(entry.getKey());
			
			args[pos] = entry.getValue();
		}
		
		Object value;
		try {
			value = this.invokeMethod.invoke(this.controller, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ServletException(e);
		}
		
		Result result = null;
		
		if(value instanceof Result) {
			if(value != null) {
				result = (Result) value;
			}
		}
		
		if(result == null) {
			result = new Result().status(200).content("No Result was returned, maybe the return type is void?");
		}
		
		if(this.produces != null) {
			result.contentType(this.produces);
		}
		
		return result;
	}

	@Override
	public Optional<Controller> getController() {
		return Optional.of(this.controller);
	}

	@Override
	public String getPathSpec() {
		return this.pathSpec;
	}
	
	/**
	 * @param pathSpec the pathSpec to set
	 */
	public void setPathSpec(String pathSpec) {
		this.pathSpec = pathSpec;
	}

	@Override
	public Pattern getCompiledPathSpec() {
		return this.regex;
	}
	
	/**
	 * @param regex the regex to set
	 */
	public void setCompiledPathSpec(Pattern regex) {
		this.regex = regex;
	}

	@Override
	public String getHttpMethod() {
		return this.httpMethod;
	}
	
	/**
	 * @param httpMethod the httpMethod to set
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	@Override
	public Optional<String> getAccepts() {
		return Optional.ofNullable(this.accepts);
	}
	
	/**
	 * @param accepts the accepts to set
	 */
	public void setAccepts(String accepts) {
		this.accepts = accepts;
	}

	@Override
	public String getProduces() {
		return this.produces;
	}
	
	/**
	 * @param produces the produces to set
	 */
	public void setProduces(String produces) {
		this.produces = produces;
	}
	
	@Override
	public Map<RequestParameter, Integer> getParameters() {
		return Collections.unmodifiableMap(this.parameters);
	}
	
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<RequestParameter, Integer> parameters) {
		this.parameters = parameters;
	}

	@Override
	public Class<?> getUnderlyingClass() {
		return this.controller.getClass();
	}

	@Override
	public Method getUnderlyingMethod() {
		return this.invokeMethod;
	}

	@Override
	public String getUnderlyingClassName() {
		return this.controller.getClass().getCanonicalName();
	}

	@Override
	public String getUnderlyingMethodName() {
		return this.invokeMethod.getName();
	}
	
	/**
	 * @param posReq the posReq to set
	 */
	public void setPosReq(int posReq) {
		this.posReq = posReq;
	}
	
	/**
	 * @param posRes the posRes to set
	 */
	public void setPosRes(int posRes) {
		this.posRes = posRes;
	}
	
	/**
	 * @param posCtx the posCtx to set
	 */
	public void setPosCtx(int posCtx) {
		this.posCtx = posCtx;
	}
	
	/**
	 * @param posParamMap the posParamMap to set
	 */
	public void setPosParamMap(int posParamMap) {
		this.posParamMap = posParamMap;
	}
	
}
