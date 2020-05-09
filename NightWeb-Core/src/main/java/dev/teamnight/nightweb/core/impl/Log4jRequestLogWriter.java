/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.RequestLog.Writer;

/**
 * @author Jonas
 *
 */
public class Log4jRequestLogWriter implements Writer {

	private final Logger LOGGER;
	
	public Log4jRequestLogWriter(Class<?> baseClass) {
		this.LOGGER = LogManager.getLogger(baseClass);
	}
	
	@Override
	public void write(String requestEntry) throws IOException {
		this.LOGGER.info(requestEntry);
	}

}
