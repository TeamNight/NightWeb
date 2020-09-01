/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.lang.reflect.Method;

/**
 * @author Jonas
 *
 */
public interface InvokedRoute extends Route {

	public Class<?> getUnderlyingClass();
	
	public Method getUnderlyingMethod();
	
	public String getUnderlyingClassName();
	
	public String getUnderlyingMethodName();
	
}
