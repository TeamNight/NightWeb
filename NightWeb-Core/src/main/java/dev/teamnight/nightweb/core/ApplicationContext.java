package dev.teamnight.nightweb.core;

import org.hibernate.SessionFactory;

public interface ApplicationContext extends Context {

	public SessionFactory getSessionFactory();
	
}
