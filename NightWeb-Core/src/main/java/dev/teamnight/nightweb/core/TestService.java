package dev.teamnight.nightweb.core;


import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.service.DatabaseService;

public class TestService extends DatabaseService<String> {

	//TODO Delete class
	
	public TestService(SessionFactory factory) {
		super(factory);
		
		System.out.println("TypeName: " + this.getType());
	}

}
