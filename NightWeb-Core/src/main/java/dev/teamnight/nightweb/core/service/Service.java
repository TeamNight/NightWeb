package dev.teamnight.nightweb.core.service;

import java.io.Serializable;
import java.util.List;

public interface Service<T> {
	
	public T getOne(Serializable key);
	public List<T> getAll();
	public List<T> getMultiple(int limit);
	public List<T> getMultiple(int offset, int limit);
	public List<T> getMultiple(String whereClause);
	public List<T> getMultiple(String whereClause, int limit);
	public List<T> getMultiple(String whereClause, int offest, int limit);
	public void save(T value);
	public Serializable create(T value);
	public void delete(T value);
	public void delete(Serializable key);
	public long count();
	public long count(String whereClause);
	
}
