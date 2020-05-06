/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.ModuleData;

/**
 * @author Jonas
 *
 */
public class ModuleService extends AbstractService<ModuleData> {

	/**
	 * @param factory
	 */
	public ModuleService(SessionFactory factory) {
		super(factory);
	}
	
	public ModuleData getByIdentifier(String identifier) {
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
		Query<ModuleData> query = session.createQuery("FROM " + ModuleData.class.getSimpleName() + " A WHERE A.identifier = :identifier", ModuleData.class);
		query.setParameter("identifier", identifier);
		
		ModuleData data = query.uniqueResult();
		session.getTransaction().commit();
		session.close();
		
		return data;
	}
	
	public List<ModuleData> getModulesByName(String name) {
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
		Query<ModuleData> query = session.createQuery("FROM " + ModuleData.class.getSimpleName() + " A WHERE A.name = :name", ModuleData.class);
		query.setParameter("name", name);
		
		List<ModuleData> dataList = query.getResultList();
		session.getTransaction().commit();
		session.close();
		
		return dataList;
	}

}
