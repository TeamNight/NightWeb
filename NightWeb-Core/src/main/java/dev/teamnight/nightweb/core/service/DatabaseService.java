/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.service;

import java.io.Serializable;
import java.util.List;

public interface DatabaseService<T> extends Service {
	
	/**
	 * Retrieves one object using the key, associations can only be retrieved when the session is active
	 * @param key
	 * @return
	 */
	public T getOne(Serializable key);
	
	/**
	 * Retrieves all objects in a table, associations can only be retrieved when the session is active.
	 * @return
	 */
	public List<T> getAll();
	
	/**
	 * Retrieves multiple objects in a table limited in number, does not fetch associations in collections. 
	 * You have to initialize them using Hibernate#initialize or attach them using getOne.
	 * @param limit
	 * @return
	 */
	public List<T> getMultiple(int limit);
	
	/**
	 * Retrieves multiple objects in a table limited in number starting at offset, does not fetch associations in collections. 
	 * You have to initialize them using Hibernate#initialize or attach them using getOne.
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<T> getMultiple(int offset, int limit);
	
	/**
	 * Retrieves multiple objects in a table which are accepted by the where clause, does not fetch associations in collections. 
	 * You have to initialize them using Hibernate#initialize or attach them using getOne.
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<T> getMultiple(String whereClause);
	
	/**
	 * Retrieves multiple objects in a table which are accepted by the where clause limited by in number, does not fetch associations in collections. 
	 * You have to initialize them using Hibernate#initialize or attach them using getOne.
	 * @param whereClause
	 * @param limit
	 * @return
	 */
	public List<T> getMultiple(String whereClause, int limit);
	
	/**
	 * Retrieves multiple objects in a table which are accepted by the where clause limited by in number starting at offset, does not fetch associations in collections. 
	 * You have to initialize them using Hibernate#initialize or attach them using getOne.
	 * @param whereClause
	 * @param offest
	 * @param limit
	 * @return
	 */
	public List<T> getMultiple(String whereClause, int offest, int limit);
	
	/**
	 * Saves or updates a value in the database.
	 * @param value
	 */
	public void save(T value);
	
	/**
	 * Saves a value in the database, use save when you are unsure if you update an already existing value
	 * @param value
	 * @return
	 */
	public Serializable create(T value);
	
	/**
	 * Removes a value from the database using the key in the object
	 * @param value
	 */
	public void delete(T value);
	
	/**
	 * Removes a value from the database using its key
	 * @param key
	 */
	public void delete(Serializable key);
	
	/**
	 * Counts the rows in the table
	 * @return
	 */
	public long count();
	
	/**
	 * Counts the rows in the table that are accepted by the where clause
	 * @param whereClause
	 * @return
	 */
	public long count(String whereClause);
	
}
