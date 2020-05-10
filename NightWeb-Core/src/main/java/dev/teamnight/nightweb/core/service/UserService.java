package dev.teamnight.nightweb.core.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.entities.User;

public class UserService extends AbstractService<User> {

	/**
	 * @param factory
	 */
	public UserService(SessionFactory factory) {
		super(factory);
	}
	
	public User getByUsername(String username) {
		return this.getByUsername(username, User.class);
	}
	
	public <T extends User> T getByUsername(String username, Class<T> type) {
		Session session = this.factory().openSession();
		session.beginTransaction();
		
		Query<T> query = session.createQuery("FROM " + type.getCanonicalName() + " U WHERE U.username = :username", type);
		query.setParameter("username", username);
		
		T user = query.uniqueResult();
		session.getTransaction().commit();
		session.close();
		
		return user;
	}

	public User getByEmail(String email) {
		return this.getByEmail(email, User.class);
	}

	public <T extends User> T getByEmail(String email, Class<T> type) {
		Session session = this.factory().openSession();
		session.beginTransaction();
		
		Query<T> query = session.createQuery("FROM " + type.getCanonicalName() + " U WHERE U.email = :email", type);
		query.setParameter("email", email);
		
		T user = query.uniqueResult();
		session.getTransaction().commit();
		session.close();
		
		return user;
	}
}
