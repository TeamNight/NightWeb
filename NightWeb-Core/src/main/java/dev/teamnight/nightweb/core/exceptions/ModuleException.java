/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.exceptions;

public class ModuleException extends RuntimeException {

	private static final long serialVersionUID = 7798342609417031449L;
	
	public ModuleException(Throwable cause, String message) {
		super(message, cause);
	}
	
	public ModuleException(Throwable cause) {
		super(cause);
	}
	
	public ModuleException(String message) {
		super(message);
	}
	
	public ModuleException() {
		super("Unknown Exception happened during Module loading");
	}
	

}
