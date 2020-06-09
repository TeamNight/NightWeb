/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.Group;
import dev.teamnight.nightweb.core.entities.User;

public class GroupService extends AbstractService<Group> {

	/**
	 * @param factory
	 */
	public GroupService(SessionFactory factory) {
		super(factory);
	}
	
	public void create(Group group, Group...groups) {
		this.create(group);
		
		for(Group g : groups) {
			this.create(g);
		}
	}
	
	@Override
	public Serializable create(Group group) {
		if(this.getByName(group.getName()) != null) {
			return null;
		}
		
		return super.create(group);
	}
	
	public Group getByName(String name) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<Group> query = session.createQuery("FROM " + this.getType().getCanonicalName() + " G WHERE G.name = :name", this.getType());
		query.setParameter("name", name);
		
		Group group = query.uniqueResult();
		session.getTransaction().commit();
		
		return group;
	}
	
	public List<Group> getStaffGroups() {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<Group> query = session.createQuery("FROM " + this.getType().getCanonicalName() + " G WHERE staffGroup = true", this.getType());
		
		List<Group> groups = query.getResultList();
		session.getTransaction().commit();
		
		return groups;
	}
	
	public long getUserCount(Group group) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<Long> query = session.createQuery("SELECT count(U) FROM " + User.class.getCanonicalName() + " U WHERE :group member of U.groups", Long.class);
		query.setParameter("group", group);
		
		Long count = query.getSingleResult();
		session.getTransaction().commit();
		
		return count;
	}
}
