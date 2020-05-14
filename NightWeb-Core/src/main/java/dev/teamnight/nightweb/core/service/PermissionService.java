/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.DefaultPermission;
import dev.teamnight.nightweb.core.entities.Permission;

public class PermissionService extends AbstractService<DefaultPermission> {

	/**
	 * @param factory
	 */
	public PermissionService(SessionFactory factory) {
		super(factory);
	}
	
	public DefaultPermission getByName(String name) {
		return this.getByName(name, DefaultPermission.class);
	}
	
	public <T extends Permission> T getByName(String name, Class<T> type) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<T> query = session.createQuery("FROM " + type.getCanonicalName() + " P WHERE P.name = :name", type);
		query.setParameter("name", name);
		
		T perm = query.uniqueResult();
		session.getTransaction().commit();
		
		return perm;
	}
	
	@Override
	public Serializable create(DefaultPermission permission) {
		if(this.getByName(permission.getName(), permission.getClass()) != null) {
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
	
	public void create(DefaultPermission permission, DefaultPermission...permissions) {
		this.create(permission);
		
		for(DefaultPermission perm : permissions) {
			this.create(perm);
		}
	}
}
