/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.UserService;

/**
 * @author Jonas
 *
 */
public class TestServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Logger logger = LogManager.getLogger();
		
		logger.debug("Session: " + ctx.getDatabaseSession());
		
		UserService serv = ctx.getServiceManager().getService(UserService.class);
		
		try {
			User user = serv.getOne(4L);
			
			user.hasPermission("test");
		} catch(LazyInitializationException e) {
			logger.debug("Caught LazyInitializationException in getOne: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
