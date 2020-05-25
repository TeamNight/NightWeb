/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.entities.Menu;
import dev.teamnight.nightweb.core.entities.MenuItem;

/**
 * @author Jonas
 *
 */
public class MenuService extends AbstractService<Menu> {

	/**
	 * @param factory
	 */
	public MenuService(SessionFactory factory) {
		super(factory);
	}
	
	public Menu getByIdentifier(String identifier) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<Menu> query = session.createQuery("FROM " + this.getType().getSimpleName() + " M WHERE M.identifier = :identifier", this.getType());
		query.setParameter("identifier", identifier);
		
		Menu menu = query.uniqueResult();
		session.getTransaction().commit();
		
		this.setLinks(menu.getNodes());
		
		return menu;
	}
	
	public boolean exists(String identifier) {
		Session session = this.factory().getCurrentSession();
		session.beginTransaction();
		
		Query<Menu> query = session.createQuery("FROM " + this.getType().getSimpleName() + " M WHERE M.identifier = :identifier", this.getType());
		query.setParameter("identifier", identifier);
		
		Menu menu = query.uniqueResult();
		session.getTransaction().commit();
		
		return menu != null;
	}
	
	@Override
	public Serializable create(Menu value) {
		if(this.exists(value.getIdentifier())) {
			return null;
		}
		
		return super.create(value);
	}
	
	public void create(Menu menu, Menu...menus) {
		this.create(menu);
		
		for(Menu m : menus) {
			this.create(m);
		}
	}
	
	private void setLinks(List<MenuItem> items) {
		for(MenuItem item : items) {
			if(item.getServletClass() != null) {
				item.setResolvedURL(NightWeb.getCoreApplication().getServer().getServletURL(item.getServletClass()));
			} else {
				item.setResolvedURL(item.getCustomURL());
			}
			
			this.setLinks(item.getChildren());
		}
	}

}
