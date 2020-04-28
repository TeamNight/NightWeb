/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import org.hibernate.SessionFactory;

public interface ApplicationContext extends Context {

	/**
	 * Returns the session factory created by the NightWeb instance.
	 * 
	 * @return {@link org.hibernate.SessionFactory} the SessionFactory
	 */
	public SessionFactory getSessionFactory();
	
}
