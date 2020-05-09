/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.Permission;

public class PermissionService extends AbstractService<Permission> {

	/**
	 * @param factory
	 */
	public PermissionService(SessionFactory factory) {
		super(factory);
	}
	
	public Permission getByName(String name) {
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
		Query<Permission> query = session.createQuery("FROM " + this.getType().getCanonicalName() + " P WHERE P.name = :name", this.getType());
		query.setParameter("name", name);
		
		Permission perm = query.uniqueResult();
		session.getTransaction().commit();
		
		return perm;
	}
	
	public void saveIfNotExists(Permission permission) {
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
		try {
			session.save(permission);
			session.getTransaction().commit();
		} catch(ConstraintViolationException e) {
		}
	}
}
