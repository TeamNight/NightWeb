/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.Optional;

/**
 * @author Jonas
 *
 */
public final class RouteQuery {
	
	public static RouteQuery of(String path) {
		return new RouteQuery(path);
	}

	private String url;
	private String httpMethod;
	private String produces;
	private String accepts;
	
	/**
	 * @param path
	 */
	public RouteQuery(String path) {
		this.url = path;
		this.httpMethod = "GET";
		this.produces = null;
		this.accepts = null;
	}

	/**
	 * @return the url
	 */
	public String url() {
		return url;
	}

	/**
	 * @return the httpMethod
	 */
	public String method() {
		return httpMethod;
	}

	/**
	 * @return the produces
	 */
	public Optional<String> produces() {
		return Optional.ofNullable(produces);
	}

	/**
	 * @return the accepts
	 */
	public Optional<String> accepts() {
		return Optional.ofNullable(accepts);
	}

	/**
	 * @param url the url to set
	 * @return this
	 */
	public RouteQuery url(String url) {
		this.url = url;
		
		return this;
	}

	/**
	 * @param httpMethod the httpMethod to set
	 * @return this
	 */
	public RouteQuery method(String httpMethod) {
		this.httpMethod = httpMethod;
		
		return this;
	}

	/**
	 * @param produces the produces to set
	 */
	public RouteQuery produces(String produces) {
		this.produces = produces;
		
		return this;
	}

	/**
	 * @param accepts the accepts to set
	 */
	public RouteQuery accepts(String accepts) {
		this.accepts = accepts;
		
		return this;
	}
	
}
