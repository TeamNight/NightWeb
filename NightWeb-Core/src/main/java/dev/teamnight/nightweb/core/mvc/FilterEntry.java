/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Jonas
 *
 */
public class FilterEntry {

	private Pattern regex;
	private List<String> httpMethods = new ArrayList<String>();
	private List<String> produces = new ArrayList<String>();
	private List<String> accepts = new ArrayList<String>();
	private Map<String, Object> attributes = new HashMap<String, Object>(); 
	
	public FilterEntry(String pattern, String... httpMethods) {
		this.regex = Pattern.compile(pattern);
		
		for(String method : httpMethods) {
			this.httpMethods.add(method);
		}
		
		if(this.httpMethods.isEmpty()) {
			this.httpMethods.add("GET");
		}
	}
	
	public FilterEntry addProduces(String produces) {
		if(!this.produces.stream().anyMatch(produces::equalsIgnoreCase)) {
			this.produces.add(produces);
		}
		
		return this;
	}
	
	public FilterEntry addAccepts(String accepts) {
		if(!this.accepts.stream().anyMatch(accepts::equalsIgnoreCase)) {
			this.accepts.add(accepts);
		}
		
		return this;
	}
	
	public FilterEntry addMethod(String method) {
		if(!this.httpMethods.stream().anyMatch(method::equalsIgnoreCase)) {
			this.httpMethods.add(method);
		}
		
		return this;
	}
	
	public FilterEntry addAttribute(String key, Object value) {
		if(!this.attributes.containsKey(key)) {
			this.attributes.put(key, value);
		}
		
		return this;
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
	public List<String> getHttpMethods() {
		return httpMethods;
	}
	
	/**
	 * @return the produces
	 */
	public List<String> getProduces() {
		return produces;
	}
	
	/**
	 * @return the accepts
	 */
	public List<String> getAccepts() {
		return accepts;
	}
	
	/**
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
}
