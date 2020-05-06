/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.ApplicationData;

/**
 * @author Jonas
 *
 */
public class ApplicationService extends AbstractService<ApplicationData> {

	/**
	 * @param factory
	 */
	public ApplicationService(SessionFactory factory) {
		super(factory);
	}
	
	/**
	 * Gets the ApplicationData from the db using an unique identifier
	 * 
	 * @param identifier
	 * @return ApplicationData or {@code null}
	 */
	public ApplicationData getByIdentifier(String identifier) {
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
		Query<ApplicationData> query = session.createQuery("FROM ApplicationData A WHERE A.identifier = :identifier", ApplicationData.class);
		query.setParameter("identifier", identifier);
		
		ApplicationData data = query.uniqueResult();
		session.getTransaction().commit();
		session.close();
		
		return data;
	}
	
	/**
	 * Gets a list of ApplicationData objects from the db using the name
	 * 
	 * @param name
	 * @return {@link java.util.List} List of ApplicationDatas
	 */
	public List<ApplicationData> getModulesByName(String name) {
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
		Query<ApplicationData> query = session.createQuery("FROM ApplicationData A WHERE A.name = :name", ApplicationData.class);
		query.setParameter("name", name);
		
		List<ApplicationData> dataList = query.getResultList();
		session.getTransaction().commit();
		session.close();
		
		return dataList;
	}
	
	@Override
	public void save(ApplicationData value) throws IllegalArgumentException {
		if(value.getModuleData() == null) {
			throw new IllegalArgumentException("ModuleData can not be null");
		}
		super.save(value);
	}

}
