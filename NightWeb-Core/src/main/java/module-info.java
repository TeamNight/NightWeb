module dev.teamnight.nightweb.core {
	exports dev.teamnight.nightweb.core;
	exports dev.teamnight.nightweb.core.entities;
	exports dev.teamnight.nightweb.core.events;
	exports dev.teamnight.nightweb.core.service;
	exports dev.teamnight.nightweb.core.template;
	exports dev.teamnight.nightweb.core.exceptions;
	exports dev.teamnight.nightweb.core.annotations;
	exports dev.teamnight.nightweb.core.util;
	exports dev.teamnight.nightweb.core.mvc;
	
	//Export to jetty in order for jetty to use the servlets
	exports dev.teamnight.nightweb.core.servlets 
		to org.eclipse.jetty.server, org.eclipse.jetty.servlet;
	exports dev.teamnight.nightweb.core.servlets.admin 
		to org.eclipse.jetty.server, org.eclipse.jetty.servlet;
	
	//Hibernate needs deep reflection access in order to instantiate objects
	opens dev.teamnight.nightweb.core.entities;
	opens dev.teamnight.nightweb.core.util;
	opens dev.teamnight.nightweb.core.module to java.xml.bind;

	requires java.naming;
	requires transitive freemarker;
	requires transitive java.persistence;
	requires java.xml.bind;
	requires transitive javax.servlet.api;
	requires org.eclipse.jetty.server;
	requires transitive org.eclipse.jetty.servlet;
	requires org.eclipse.jetty.util;
	requires transitive org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires transitive org.hibernate.orm.core;
	
	//For Hibernate
	requires net.bytebuddy;
	requires transitive mysql.connector.java;
	requires com.google.common;
	requires commons.validator;
	requires owasp.java.html.sanitizer;
	requires java.sql;
	requires org.eclipse.jetty.http;
	requires gson;
	requires java.base;

}