/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.entities.ModuleData;

/**
 * @author Jonas
 *
 */
public class ModuleService extends DatabaseService<ModuleData> {

	/**
	 * @param factory
	 */
	public ModuleService(SessionFactory factory) {
		super(factory);
	}

}
