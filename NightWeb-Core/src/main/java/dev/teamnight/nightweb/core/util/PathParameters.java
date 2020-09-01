/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonas
 *
 */
public class PathParameters {

	private List<String> parameters = new ArrayList<String>();
	
	// -> /activation/admin@teamnight.dev/abcdef
	// 1. activation/admin@teamnight.dev/abcdef
	// 2. (activation, admin@teamnight.dev, abcdef)
	// 3. (1: activation, 2: admin@teamnight.dev, 3: abcdef)
	public PathParameters(String pathInfo) {
		if(pathInfo == null) {
			return;
		}
		
		pathInfo = pathInfo.substring(1);
		
		if(pathInfo.isBlank()) {
			return;
		}
		
		if(pathInfo.contains("/")) {
			String[] params = pathInfo.split("/");
			
			for(String param : params) {
				this.parameters.add(param);
			}
		} else {
			this.parameters.add(pathInfo);
		}
	}
	
	public String getParameter(int index) {
		if(index < this.parameters.size()) {
			return parameters.get(index);
		} else {
			return null;
		}
	}
	
	public int size() {
		return this.parameters.size();
	}
	
}
