module dev.teamnight.nightweb.core {
	exports dev.teamnight.nightweb.core.service;
	exports dev.teamnight.nightweb.core;
	exports dev.teamnight.nightweb.core.entities;
	
	//Export to jetty in order for jetty to use the servlets
	exports dev.teamnight.nightweb.core.servlets 
		to jetty.server, jetty.servlet;
	
	//Hibernate needs deep reflection access in order to instantiate objects
	opens dev.teamnight.nightweb.core.entities;

	requires java.naming;
	requires transitive java.persistence;
	requires java.xml.bind;
	requires transitive javax.servlet.api;
	requires jetty.server;
	requires jetty.servlet;
	requires jetty.util;
	requires log4j.api;
	requires transitive org.hibernate.orm.core;
	
	//For Hibernate
	requires net.bytebuddy;
	requires transitive mysql.connector.java;
}