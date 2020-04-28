/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.module;

/**
 * @author Jonas Müller
 *
 */
public class UnknownDependencyException extends RuntimeException {

	private static final long serialVersionUID = 613961438119046876L;

	public UnknownDependencyException(Throwable cause, String message) {
		super(message, cause);
	}
	
	public UnknownDependencyException(Throwable cause) {
		super(cause);
	}
	
	public UnknownDependencyException(String message) {
		super(message);
	}
	
}
