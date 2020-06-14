/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.events;

import java.util.Optional;
import java.util.regex.Pattern;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.mvc.MethodHolder;

/**
 * @author Jonas
 *
 */
public class RouteAddedEvent implements Event {

	private Context ctx;
	
	private Pattern pattern;
	private String pathSpec;
	private String httpMethod;
	private String produces;
	private String accepts;
	
	public RouteAddedEvent(MethodHolder holder) {
		this.ctx = holder.getContext();
		this.pattern = holder.getRegex();
		this.pathSpec = holder.getPathSpec();
		this.httpMethod = holder.getHttpMethod();
		this.produces = holder.getProduces();
		this.accepts = holder.getAccepts().orElse(null);
	}
	
	@Override
	public Optional<Context> getContext() {
		return Optional.ofNullable(this.ctx);
	}
	
	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}
	
	/**
	 * @return the pathSpec
	 */
	public String getPathSpec() {
		return pathSpec;
	}
	
	/**
	 * @return the httpMethod
	 */
	public String getHttpMethod() {
		return httpMethod;
	}
	
	/**
	 * @return the produces
	 */
	public String getProduces() {
		return produces;
	}
	
	/**
	 * @return the accepts
	 */
	public String getAccepts() {
		return accepts;
	}

}
