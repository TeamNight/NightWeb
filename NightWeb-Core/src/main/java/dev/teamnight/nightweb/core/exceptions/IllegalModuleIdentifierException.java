/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.exceptions;

/**
 * @author Jonas
 *
 */
public class IllegalModuleIdentifierException extends RuntimeException {

	private static final long serialVersionUID = -2712567984791959692L;
	
	public IllegalModuleIdentifierException(String message) {
		super(message);
	}
	
	public IllegalModuleIdentifierException() {
		super("Illegal module identifier");
	}

}
