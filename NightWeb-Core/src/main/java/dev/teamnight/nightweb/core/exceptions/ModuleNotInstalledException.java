/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.exceptions;

/**
 * @author Jonas
 *
 */
public class ModuleNotInstalledException extends RuntimeException {

	public ModuleNotInstalledException(String message) {
		super(message);
	}
	
}
