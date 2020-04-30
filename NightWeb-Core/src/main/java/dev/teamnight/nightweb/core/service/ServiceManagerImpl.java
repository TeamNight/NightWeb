/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.hibernate.SessionFactory;

public class ServiceManagerImpl implements ServiceManager {
	
	private SessionFactory sessionFactory;
	
	private Map<String, Service<?>> services = new HashMap<String, Service<?>>();
	
	public ServiceManagerImpl(SessionFactory factory) {
		this.sessionFactory = factory;
	}
	
	@Override
	public void register(Service<?> service) {
		LogManager.getLogger().info("Registering service >> " + service.getClass().getCanonicalName());
		this.services.put(service.getClass().getCanonicalName(), service);
	}

	@Override
	public void register(Class<? extends Service<?>> service) throws IllegalArgumentException {
		LogManager.getLogger().info("Registering service >> " + service.getCanonicalName());
		Constructor<?> cs;
		
		try {
			cs = service.getConstructor(SessionFactory.class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Class " + service.getCanonicalName() + " does not have public " + service.getSimpleName() + "(SessionFactory) constructor", e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Could not access constructor " + service.getSimpleName() + "(SessionFactory). Might be protected or private.", e);
		}
		
		try {
			Service<?> serviceInstance = (Service<?>) cs.newInstance(this.sessionFactory);
			
			this.services.put(service.getCanonicalName(), serviceInstance);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new IllegalArgumentException("Exception caught while creating new instance: " + e.getMessage(), e);
		}
	}

	@Override
	public <T extends Service<?>> T getService(Class<T> serviceClass) {
		return this.getService(serviceClass.getCanonicalName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service<?>> T getService(String canocialClassName) {
		Service<?> service = services.get(canocialClassName);
		
		if(service != null) {
			return (T) service;
		}
		
		return null;
	}

	@Override
	public List<Service<?>> getServices() {
		return Collections.unmodifiableList(this.services.values().stream().collect(Collectors.toList()));
	}

}
