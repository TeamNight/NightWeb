/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.teamnight.nightweb.core.Context;

/**
 * @author Jonas
 *
 */
public class MethodHolder {

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
	
	public Result executeMethod(HttpServletRequest req, HttpServletResponse res, Map<RequestParameter, String> parameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
		
		Object value = this.invokeMethod.invoke(this.controller, args);
		
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
	
	/**
	 * @return the ctx
	 */
	public Context getContext() {
		return ctx;
	}
	
	/**
	 * @return the invokeMethod
	 */
	public Method getMethod() {
		return invokeMethod;
	}
	
	/**
	 * @return the controller
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * @return the pathSpec
	 */
	public String getPathSpec() {
		return pathSpec;
	}

	/**
	 * @return the regex
	 */
	public Pattern getRegex() {
		return regex;
	}

	/**
	 * @return the httpMethod
	 */
	public String getHttpMethod() {
		return httpMethod;
	}
	
	/**
	 * @return the accepts
	 */
	public Optional<String> getAccepts() {
		return Optional.ofNullable(this.accepts);
	}
	
	/**
	 * @return the produces
	 */
	public Optional<String> getProduces() {
		return Optional.ofNullable(this.produces);
	}

	/**
	 * @return the parameters
	 */
	public Map<RequestParameter, Integer> getParameters() {
		return parameters;
	}

	/**
	 * @return the posReq
	 */
	public int getPosReq() {
		return posReq;
	}

	/**
	 * @return the posRes
	 */
	public int getPosRes() {
		return posRes;
	}

	/**
	 * @return the posCtx
	 */
	public int getPosCtx() {
		return posCtx;
	}

	/**
	 * @return the posParamMap
	 */
	public int getPosParamMap() {
		return posParamMap;
	}

	/**
	 * @param pathSpec the pathSpec to set
	 */
	public void setPathSpec(String pathSpec) {
		this.pathSpec = pathSpec;
	}

	/**
	 * @param regex the regex to set
	 */
	public void setRegex(Pattern regex) {
		this.regex = regex;
	}

	/**
	 * @param httpMethod the httpMethod to set
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	/**
	 * @param accepts the accepts to set
	 */
	public void setAccepts(String accepts) {
		this.accepts = accepts;
	}
	
	/**
	 * @param produces the produces to set
	 */
	public void setProduces(String produces) {
		this.produces = produces;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Map<RequestParameter, Integer> parameters) {
		this.parameters = parameters;
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
