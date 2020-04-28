/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.entities.ApplicationData;

/**
 * @author Jonas
 *
 */
public class ApplicationService extends DatabaseService<ApplicationData> {

	/**
	 * @param factory
	 */
	public ApplicationService(SessionFactory factory) {
		super(factory);
	}

}
