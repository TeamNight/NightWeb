/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.Setting;

/**
 * @author Jonas
 *
 */
public class SettingService extends AbstractService<Setting> {

	private Map<String, Setting> loadedSettings = new HashMap<String, Setting>();
	
	/**
	 * @param factory
	 */
	public SettingService(SessionFactory factory) {
		super(factory);
	}
	
	
	public Setting getByKey(String key) {
		return this.getByKey(key, false);
	}
	
	public Setting getByKey(String key, boolean forceDB) {
		if(this.loadedSettings.containsKey(key) && !forceDB) {
			return this.loadedSettings.get(key);
		}
		
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<Setting> query = session.createQuery("FROM " + this.getType().getSimpleName() + " S WHERE S.key = :key", this.getType());
		query.setParameter("key", key);
		
		Setting setting = query.uniqueResult();
		session.getTransaction().commit();
		
		return setting;
	}

	@Override
	public Setting getOne(Serializable key) {
		Setting setting = super.getOne(key);
		
		this.addToMap(setting);
		
		return setting;
	}

	@Override
	public List<Setting> getAll() {
		List<Setting> list = super.getAll();
		
		for(Setting setting : list) {
			this.addToMap(setting);
		}
		
		return list;
	}
	
	@Override
	public List<Setting> getMultiple(int offset, int limit) {
		List<Setting> list = super.getMultiple(offset, limit);
		
		for(Setting setting : list) {
			this.addToMap(setting);
		}
		
		return list;
	}
	
	@Override
	public List<Setting> getMultiple(String whereClause, int offset, int limit) {
		List<Setting> list = super.getMultiple(whereClause, offset, limit);
		
		for(Setting setting : list) {
			this.addToMap(setting);
		}
		
		return list;
	}
	
	@Override
	public Serializable create(Setting value) {
		if(this.getByKey(value.getKey(), false) != null) {
			return null;
		}
		
		Serializable key = super.create(value);
		
		this.getOne(key);
		
		return key;
	}
	
	public void create(Setting value, Setting... values) {
		this.create(value);
		
		for(Setting val : values) {
			this.create(val);
		}
	}
	
	public void save(Setting value, Setting... values) {
		this.save(value);
		
		for(Setting val : values) {
			this.save(val);
		}
	}
	
	@Override
	public void delete(Serializable key) {
		super.delete(key);
		this.loadedSettings.remove(key);
	}
	
	@Override
	public void delete(Setting value) {
		super.delete(value);
		this.loadedSettings.remove(value.getKey());
	}
	
	
	/**
	 * 
	 */
	private void addToMap(Setting setting) {
		if(!this.loadedSettings.containsKey(setting.getKey())) {
			this.loadedSettings.put(setting.getKey(), setting);
		}
	}
	
	public void refresh() {
		this.loadedSettings.clear();
		this.getAll();
	}
	
}
