/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
/**
 * Annotation to mark a method as require authorization.
 * Methods with this annotation will be checked for an
 * active session and if the user has the permission that
 * is set as value.
 * @author Jonas
 *
 */
public @interface Authorized {

	String value() default "nightweb.admin.canUseACP";
	
}
