/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<Permission> query = session.createQuery("FROM " + this.getType().getCanonicalName() + " P WHERE P.name = :name", this.getType());
		query.setParameter("name", name);
		
		Permission perm = query.uniqueResult();
		session.getTransaction().commit();
		
		return perm;
	}
	
	@Override
	public Serializable create(Permission permission) {
		if(this.getByName(permission.getName()) != null) {
			return null;
		}
		
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Serializable key = null;
		try {
			key = session.save(permission);
			session.getTransaction().commit();
		} catch(Exception e) {
			session.getTransaction().rollback();
			throw e;
		}
		
		return key;
	}
	
	public void create(Permission permission, Permission...permissions) {
		this.create(permission);
		
		for(Permission perm : permissions) {
			this.create(perm);
		}
	}
}
