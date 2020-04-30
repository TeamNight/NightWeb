/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.forum;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.ApplicationContext;

/**
 * @author Jonas
 *
 */
public class ForumApplication extends Application {

	private static final Logger LOGGER = LogManager.getLogger(ForumApplication.class);
	
	@Override
	public void configure(List<Class<?>> entityList) {
		LOGGER.info("Configuring was called!");
	}

	@Override
	public void init(ApplicationContext ctx) {
		LOGGER.info("Starting Forum Application");
	}

}
