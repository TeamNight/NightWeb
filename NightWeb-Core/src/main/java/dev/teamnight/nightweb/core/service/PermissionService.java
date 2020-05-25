/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.DefaultPermission;
import dev.teamnight.nightweb.core.entities.Permission;
import dev.teamnight.nightweb.core.entities.PermissionCategory;

public class PermissionService extends AbstractService<DefaultPermission> {

	/**
	 * @param factory
	 */
	public PermissionService(SessionFactory factory) {
		super(factory);
	}
	
	public PermissionCategory getCategory(Serializable key) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		PermissionCategory entity = session.get(PermissionCategory.class, key);
		session.getTransaction().commit();
		
		return entity;
	}
	
	public PermissionCategory getCategoryByName(String name) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<PermissionCategory> query = session.createQuery("FROM " + PermissionCategory.class.getCanonicalName() + " P WHERE P.name = :name", PermissionCategory.class);
		query.setParameter("name", name);
		
		PermissionCategory perm = query.uniqueResult();
		session.getTransaction().commit();
		
		return perm;
	}
	
	public List<PermissionCategory> getCategories() {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<PermissionCategory> query = session.createQuery("FROM " + PermissionCategory.class.getCanonicalName() + " P ORDER BY P.parent, P.showOrder NULLS FIRST", PermissionCategory.class);
		
		List<PermissionCategory> perms = query.getResultList();
		session.getTransaction().commit();
		
		return perms;
	}
	
	public Serializable createCategory(PermissionCategory category) {
		if(this.getCategoryByName(category.getName()) != null) {
			return null;
		}
		
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Serializable key = null;
		try {
			key = session.save(category);
			session.getTransaction().commit();
		} catch(Exception e) {
			session.getTransaction().rollback();
			throw e;
		}
		
		return key;
	}
	
	public void saveCategory(PermissionCategory category) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		try {
			session.saveOrUpdate(category);
			session.getTransaction().commit();
		} catch(Exception e) {
			session.getTransaction().rollback();
			throw e;
		}
	}
	
	public void createCategories(PermissionCategory category, PermissionCategory...permissionCategories) {
		this.createCategory(category);
		
		for(PermissionCategory cat : permissionCategories) {
			this.createCategory(cat);
		}
	}
	
	public void saveCategories(PermissionCategory category, PermissionCategory...permissionCategories) {
		this.saveCategory(category);
		
		for(PermissionCategory cat : permissionCategories) {
			this.saveCategory(cat);
		}
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
	public List<DefaultPermission> getAll() {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<DefaultPermission> query = session.createQuery("FROM " + this.getType().getCanonicalName() + " P ORDER BY P.category asc, P.showOrder asc", this.getType());
		
		List<DefaultPermission> perm = query.getResultList();
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
