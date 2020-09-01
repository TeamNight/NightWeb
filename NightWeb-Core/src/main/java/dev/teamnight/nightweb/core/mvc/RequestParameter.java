/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.Objects;

/**
 * @author Jonas
 *
 */
public final class RequestParameter {
	
	private boolean urlParam = false;
	private String name;
	
	/**
	 * @param name
	 * @param value
	 */
	public RequestParameter(String name) {
		this.name = name;
	}
	
	/**
	 * @param urlParam
	 * @param name
	 * @param value
	 */
	public RequestParameter(boolean urlParam, String name) {
		this.urlParam = urlParam;
		this.name = name;
	}

	/**
	 * @return the urlParam
	 */
	public boolean isURLParameter() {
		return urlParam;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param urlParam the urlParam to set
	 */
	public void setUrlParam(boolean urlParam) {
		this.urlParam = urlParam;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(!(obj instanceof RequestParameter)) {
			return false;
		}
		
		RequestParameter other = (RequestParameter) obj;
		
		return Objects.equals(this.urlParam, other.urlParam)
				&& Objects.equals(this.name, other.name);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Boolean.hashCode(this.urlParam);
		hash = 31 * hash + (this.name == null ? 0 : this.name.hashCode());
		return hash;
	}
	
}
