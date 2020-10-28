/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.regex.Pattern;

/**
 * @author Jonas
 *
 */
public interface RoutePath {

	public String getPathSpec();
	
	public Pattern getPattern(boolean withContextPath);
	
}
