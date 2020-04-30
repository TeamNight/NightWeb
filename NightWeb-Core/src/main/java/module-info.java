module dev.teamnight.nightweb.core {
	exports dev.teamnight.nightweb.core;
	exports dev.teamnight.nightweb.core.entities;
	exports dev.teamnight.nightweb.core.service;
	
	//Export to jetty in order for jetty to use the servlets
	exports dev.teamnight.nightweb.core.servlets 
		to org.eclipse.jetty.server, org.eclipse.jetty.servlet;
	
	//Hibernate needs deep reflection access in order to instantiate objects
	opens dev.teamnight.nightweb.core.entities;
	opens dev.teamnight.nightweb.core.module to java.xml.bind;

	requires java.naming;
	requires transitive java.persistence;
	requires java.xml.bind;
	requires transitive javax.servlet.api;
	requires org.eclipse.jetty.server;
	requires org.eclipse.jetty.servlet;
	requires org.eclipse.jetty.util;
	requires log4j.api;
	requires log4j.core;
	requires transitive org.hibernate.orm.core;
	
	//For Hibernate
	requires net.bytebuddy;
	requires transitive mysql.connector.java;
}