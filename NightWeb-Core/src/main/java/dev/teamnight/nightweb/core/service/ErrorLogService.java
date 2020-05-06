/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.entities.ErrorLogEntry;

/**
 * @author Jonas
 *
 */
public class ErrorLogService extends AbstractService<ErrorLogEntry> {

	/**
	 * @param factory
	 */
	public ErrorLogService(SessionFactory factory) {
		super(factory);
	}
	
}
