/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.util.Optional;

/**
 * A class providing thread-local access in order to provide the request handling
 * with access to important variables that are only available due to the
 * HttpRequest.
 * 
 * e.g. Hibernate Envers Listeners or anything else with non-direct access to the
 * HttpRequest.
 * 
 * @author Jonas
 *
 */
public class RequestContext {

	private static ThreadLocal<Context> moduleContext = new ThreadLocal<>();
	private static ThreadLocal<Authenticator> authenticator = new ThreadLocal<>();
	
	public static Context getModuleContext() {
		return RequestContext.moduleContext.get();
	}
	
	public static Optional<Authenticator> getAuthenticator() {
		return Optional.ofNullable(RequestContext.authenticator.get());
	}
	
	public static void setModuleContext(Context ctx) {
		RequestContext.moduleContext.set(ctx);
	}
	
	public static void setAuthenticator(Authenticator auth) {
		RequestContext.authenticator.set(auth);
	}
}
