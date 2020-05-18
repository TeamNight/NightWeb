/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.SystemSetting;

/**
 * @author Jonas
 *
 */
public class SettingService extends AbstractService<SystemSetting> {

	private Map<String, SystemSetting> loadedSettings = new HashMap<String, SystemSetting>();
	
	/**
	 * @param factory
	 */
	public SettingService(SessionFactory factory) {
		super(factory);
	}
	
	
	public SystemSetting getByKey(String key) {
		return this.getByKey(key, false);
	}
	
	public SystemSetting getByKey(String key, boolean forceDB) {
		if(this.loadedSettings.containsKey(key) && !forceDB) {
			return this.loadedSettings.get(key);
		}
		
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<SystemSetting> query = session.createQuery("FROM " + this.getType().getSimpleName() + " S WHERE S.key = :key", this.getType());
		query.setParameter("key", key);
		
		SystemSetting setting = query.uniqueResult();
		session.getTransaction().commit();
		
		return setting;
	}
	
	public Set<String> getCategories() {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<String> query = session.createQuery("SELECT S.category FROM " + this.getType().getSimpleName() + " S", String.class);
		List<String> results = query.getResultList();
		
		Set<String> categories = new LinkedHashSet<String>();
		
		for(String result : results) {
			categories.add(result);
		}
		
		session.getTransaction().commit();
		
		return categories;
	}
	
	public List<SystemSetting> getByCategory(String category) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<SystemSetting> query = session.createQuery("FROM " + this.getType().getSimpleName() + " S WHERE category LIKE :category", this.getType());
		query.setParameter("category", "%" + category + "%");
		
		List<SystemSetting> results = query.getResultList();
		session.getTransaction().commit();
		
		return results;
	}

	@Override
	public SystemSetting getOne(Serializable key) {
		SystemSetting setting = super.getOne(key);
		
		this.addToMap(setting);
		
		return setting;
	}

	@Override
	public List<SystemSetting> getAll() {
		List<SystemSetting> list = super.getAll();
		
		for(SystemSetting setting : list) {
			this.addToMap(setting);
		}
		
		return list;
	}
	
	@Override
	public List<SystemSetting> getMultiple(int offset, int limit) {
		List<SystemSetting> list = super.getMultiple(offset, limit);
		
		for(SystemSetting setting : list) {
			this.addToMap(setting);
		}
		
		return list;
	}
	
	@Override
	public List<SystemSetting> getMultiple(String whereClause, int offset, int limit) {
		List<SystemSetting> list = super.getMultiple(whereClause, offset, limit);
		
		for(SystemSetting setting : list) {
			this.addToMap(setting);
		}
		
		return list;
	}
	
	@Override
	public Serializable create(SystemSetting value) {
		if(this.getByKey(value.getKey(), false) != null) {
			return null;
		}
		
		Serializable key = super.create(value);
		
		this.getOne(key);
		
		return key;
	}
	
	public void create(SystemSetting value, SystemSetting... values) {
		this.create(value);
		
		for(SystemSetting val : values) {
			this.create(val);
		}
	}
	
	public void save(SystemSetting value, SystemSetting... values) {
		this.save(value);
		
		for(SystemSetting val : values) {
			this.save(val);
		}
	}
	
	@Override
	public void delete(Serializable key) {
		super.delete(key);
		this.loadedSettings.remove(key);
	}
	
	@Override
	public void delete(SystemSetting value) {
		super.delete(value);
		this.loadedSettings.remove(value.getKey());
	}
	
	
	/**
	 * 
	 */
	private void addToMap(SystemSetting setting) {
		if(!this.loadedSettings.containsKey(setting.getKey())) {
			this.loadedSettings.put(setting.getKey(), setting);
		}
	}
	
	public void refresh() {
		this.loadedSettings.clear();
		this.getAll();
	}
	
}
