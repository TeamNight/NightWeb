package dev.teamnight.nightweb.core.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.entities.User;

public class UserService extends AbstractService<User> {

	/**
	 * @param factory
	 */
	public UserService(SessionFactory factory) {
		super(factory);
	}
	
	public void loadCollections(User user) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		User loaded = session.get(User.class, user.getId());
		user.setGroups(loaded.getGroups());
		user.setPermissions(loaded.getPermissions());
		
		session.getTransaction().commit();
	}
	
	public User getByUsername(String username) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		User user = session.createQuery("FROM " + this.getType().getCanonicalName() + " U "
				+ "LEFT JOIN FETCH U.groups G "
				+ "WHERE U.username = :username",  this.getType())
				.setParameter("username", username)
				.uniqueResult();
		
		user = session.createQuery("FROM " + this.getType().getCanonicalName() + " U "
				+ "LEFT JOIN FETCH U.permissions P "
				+ "WHERE U in :user", this.getType())
				.setParameter("user", user)
				.uniqueResult();
		
		session.getTransaction().commit();
		
		return user;
	}

	public User getByEmail(String email) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		User user = session.createQuery("FROM " + this.getType().getCanonicalName() + " U "
				+ "LEFT JOIN FETCH U.groups G "
				+ "WHERE U.email = :email",  this.getType())
				.setParameter("email", email)
				.uniqueResult();
		
		user = session.createQuery("FROM " + this.getType().getCanonicalName() + " U "
				+ "LEFT JOIN FETCH U.permissions P "
				+ "WHERE U in :user", this.getType())
				.setParameter("user", user)
				.uniqueResult();
		
		session.getTransaction().commit();
		
		return user;
	}
}
