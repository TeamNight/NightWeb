/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.Group;

public class GroupService extends AbstractService<Group> {

	/**
	 * @param factory
	 */
	public GroupService(SessionFactory factory) {
		super(factory);
	}
	
	public Group getByName(String name) {
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
		Query<Group> query = session.createQuery("FROM " + this.getType().getCanonicalName() + " G WHERE G.name = :name", this.getType());
		query.setParameter("name", name);
		
		Group group = query.uniqueResult();
		session.getTransaction().commit();
		
		return group;
	}
	
	public List<Group> getStaffGroups() {
		Session session = this.getSessionFactory().openSession();
		session.beginTransaction();
		
		Query<Group> query = session.createQuery("FROM " + this.getType().getCanonicalName() + " G WHERE staffGroup = true", this.getType());
		
		List<Group> groups = query.getResultList();
		
		session.getTransaction().commit();
		
		return groups;
	}
}
