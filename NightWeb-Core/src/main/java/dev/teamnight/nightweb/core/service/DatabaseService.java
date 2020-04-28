/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public abstract class DatabaseService<T> implements Service<T> {

	private final Class<T> type;
	private SessionFactory factory;
	
	@SuppressWarnings("unused")
	private DatabaseService() {
		this.type = null;
	}
	
	@SuppressWarnings("unchecked")
	public DatabaseService(SessionFactory factory) {
		//TODO remove comment
//		if(factory == null) {
//			throw new IllegalArgumentException("factory can not be null");
//		}
		
		this.type = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.factory = factory;
	}
	
	public Class<T> getType() {
		return type;
	}
	
	@Override
	public T getOne(Serializable key) {
		Session session = factory.openSession();
		session.beginTransaction();
		T entity = session.get(this.type, key);
		session.getTransaction().commit();
		
		return entity;
	}
	
	@Override
	public List<T> getAll() {
		Session session = factory.openSession();
		
		CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(this.type);
		criteria.from(this.type);
		
		session.beginTransaction();
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
		Session session = factory.openSession();
		
		CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(this.type);
		criteria.from(this.type);
		
		session.beginTransaction();
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
		Session session = factory.openSession();
		
		Query<T> query = session.createQuery("FROM " + this.type.getSimpleName() + " WHERE " + whereClause, this.type);
		
		session.beginTransaction();
		List<T> entityList = query
				.setMaxResults(limit)
				.setFirstResult(offset)
				.getResultList();
		
		session.getTransaction().commit();
		
		return entityList;
	}
	
	@Override
	public Serializable create(T value) {
		Session session = factory.openSession();
		
		session.beginTransaction();
		Serializable id = session.save(value);
		session.getTransaction().commit();
		
		return id;
	}
	
	@Override
	public void save(T value) {
		Session session = factory.openSession();
		
		session.beginTransaction();
		session.saveOrUpdate(value);
		session.getTransaction().commit();
	}
	
	@Override
	public void delete(Serializable key) {
		Session session = factory.openSession();
		
		session.beginTransaction();
		T entity = session.load(this.type, key);
		session.delete(entity);
		session.getTransaction().commit();
	}
	
	@Override
	public void delete(T value) {
		Session session = factory.openSession();
		
		session.beginTransaction();
		session.delete(value);
		session.getTransaction().commit();
	}
	
	@Override
	public long count() {
		Session session = factory.openSession();
		
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
		Session session = factory.openSession();
		
		Query<Long> query = session.createQuery("SELECT COUNT(*) FROM " + this.type.getSimpleName() + " WHERE " + whereClause, Long.class);
		
		session.beginTransaction();
		Long count = query
				.getSingleResult();
		
		session.getTransaction().commit();
		
		return count;
	}
}
