/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.util.List;

public interface ServiceManager {

	public void register(Service service);
	
	public void register(Class<? extends Service> service) throws IllegalArgumentException;
	
	public <T extends Service> T getService(Class<T> serviceClass);
	
	public <T extends Service> T getService(String canocialClassName);
	
	public List<Service> getServices();
	
}
