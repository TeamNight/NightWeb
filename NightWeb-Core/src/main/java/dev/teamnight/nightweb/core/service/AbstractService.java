/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

/**
 * This implementation provides useful methods for services. If you use collections within your entity, you need to implement own methods to initialize the collections
 * either using Hibernate#initialize or by using LEFT JOIN FETCH in a HQL Query. You may provide various methods for getting the Entity with and without the collections
 * loaded.
 * 
 * You can create a method that loads these collections like the example of UserService:
 * <pre>
 * public void loadCollections(User user) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		User loaded = session.get(User.class, user.getId());
		user.setGroups(loaded.getGroups());
		user.setPermissions(loaded.getPermissions());
		
		session.getTransaction().commit();
	}
 * </pre>
 * 
 * @author Jonas
 *
 * @param <T>
 */
public abstract class AbstractService<T> implements Service<T> {

	private final Class<T> type;
	private SessionFactory factory;
	
	@SuppressWarnings("unused")
	private AbstractService() {
		this.type = null;
	}
	
	@SuppressWarnings("unchecked")
	public AbstractService(SessionFactory factory) {
		if(factory == null) {
			throw new IllegalArgumentException("factory can not be null");
		}
		
		this.type = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.factory = factory;
	}
	
	public Class<T> getType() {
		return type;
	}
	
	@Override
	public T getOne(Serializable key) {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		
		T entity = session.get(this.type, key);
		Hibernate.initialize(entity); //does not work
		
		session.getTransaction().commit();
		
		return entity;
	}
	
	@Override
	public List<T> getAll() {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		
		CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(this.type);
		criteria.from(this.type);
		
		List<T> entityList = session.createQuery(criteria).getResultList();
		session.getTransaction().commit();
		
		return entityList;
	}
	
	@Override
	public List<T> getMultiple(int limit) {
		return this.getMultiple(0, limit);
	}
	
	@Override
	public List<T> getMultiple(int offset, int limit) {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		
		CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(this.type);
		criteria.from(this.type);
		
		List<T> entityList = session
				.createQuery(criteria)
				.setMaxResults(limit)
				.setFirstResult(offset)
				.getResultList();
		
		session.getTransaction().commit();
		
		return entityList;
	}
	
	@Override
	public List<T> getMultiple(String whereClause) {
		return this.getMultiple(whereClause, 100);
	}
	
	@Override
	public List<T> getMultiple(String whereClause, int limit) {
		return this.getMultiple(whereClause, 0, limit);
	}
	
	@Override
	public List<T> getMultiple(String whereClause, int offset, int limit) {
		Session session = factory.getCurrentSession();
		session.beginTransaction();
		
		Query<T> query = session.createQuery("FROM " + this.type.getSimpleName() + " WHERE " + whereClause, this.type);

		List<T> entityList = query
				.setMaxResults(limit)
				.setFirstResult(offset)
				.getResultList();
		
		session.getTransaction().commit();
		
		return entityList;
	}
	
	@Override
	public Serializable create(T value) {
		Session session = factory.getCurrentSession();
		
		Serializable id = null;
		try {
			session.beginTransaction();
			id = session.save(value);
			session.getTransaction().commit();
		} catch(Exception e) {
			if(session.getTransaction() != null) {
				session.getTransaction().rollback();
				throw e;
			}
		}
		
		return id;
	}
	
	@Override
	public void save(T value) {
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			session.saveOrUpdate(value);
			session.getTransaction().commit();
		} catch(Exception e) {
			if(session.getTransaction() != null) {
				session.getTransaction().rollback();
				throw e;
			}
		}
	}
	
	@Override
	public void delete(Serializable key) {
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			T entity = session.load(this.type, key);
			session.delete(entity);
			session.getTransaction().commit();
		} catch(Exception e) {
			if(session.getTransaction() != null) {
				session.getTransaction().rollback();
				throw e;
			}
		}
	}
	
	@Override
	public void delete(T value) {
		Session session = factory.getCurrentSession();
		
		try {
			session.beginTransaction();
			session.delete(value);
			session.getTransaction().commit();
		} catch(Exception e) {
			if(session.getTransaction() != null) {
				session.getTransaction().rollback();
				throw e;
			}
		}
	}
	
	@Override
	public long count() {
		Session session = factory.getCurrentSession();
		
		CriteriaQuery<Long> criteria = session.getCriteriaBuilder().createQuery(Long.class);
		Root<T> root = criteria.from(this.type);
		criteria.select(session.getCriteriaBuilder().count(root));
		
		session.beginTransaction();
		Long count = session.createQuery(criteria).getSingleResult();
		session.getTransaction().commit();
		
		return count;
	}
	
	@Override
	public long count(String whereClause) {
		Session session = factory.getCurrentSession();
		
		Query<Long> query = session.createQuery("SELECT COUNT(*) FROM " + this.type.getSimpleName() + " WHERE " + whereClause, Long.class);
		
		session.beginTransaction();
		Long count = query
				.getSingleResult();
		
		session.getTransaction().commit();
		
		return count;
	}
	
	protected SessionFactory factory() {
		return this.factory;
	}
}
