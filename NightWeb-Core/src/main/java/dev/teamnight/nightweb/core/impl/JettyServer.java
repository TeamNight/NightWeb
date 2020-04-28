/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.NightWebCore;
import dev.teamnight.nightweb.core.Server;
import dev.teamnight.nightweb.core.entities.XmlConfiguration;

public class JettyServer implements Server {

	private org.eclipse.jetty.server.Server server;
	
	private ContextHandlerCollection servletContextHandlers = new ContextHandlerCollection();
	
	private XmlConfiguration conf;
	
	private NightWebCore core;
	
	public JettyServer(NightWebCore core, XmlConfiguration conf) {
		this.core = core;
		this.conf = conf;
		
		//Thread pool for connectors
		QueuedThreadPool threadPool = new QueuedThreadPool(conf.getMaxThreads(), conf.getMinThreads());
		
		//Jetty Server
		this.server = new org.eclipse.jetty.server.Server(threadPool);
		
		//Connector for HTTP requests
		HttpConfiguration httpConf = new HttpConfiguration();
		httpConf.setSecureScheme("https");
		httpConf.setSecurePort(conf.getSslPort());
		
		ServerConnector connector = new ServerConnector(this.server,
				new HttpConnectionFactory(httpConf));
		connector.setHost(conf.getHostIPAddress());
		connector.setPort(conf.getPort());
		connector.setName("unsecure");
		
		this.server.addConnector(connector);
		
		HandlerList handlers = new HandlerList();
		
		//SSL Support
		if(this.isSSLEnabled()) {
			HttpConfiguration httpsConf = new HttpConfiguration(httpConf);
			httpsConf.addCustomizer(new SecureRequestCustomizer());
			
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStorePath(conf.getKeystoreFile());
			sslContextFactory.setKeyStorePassword(conf.getKeystorePassword());
			
			ServerConnector sslConnector = new ServerConnector(this.server,
					new SslConnectionFactory(sslContextFactory, "http/1.1"),
					new HttpConnectionFactory(httpsConf));
			sslConnector.setHost(conf.getHostIPAddress());
			sslConnector.setPort(conf.getSslPort());
			sslConnector.setName("secure");
			
			this.server.addConnector(sslConnector);
			
			ContextHandler sslRedirectHandler = new ContextHandler();
			sslRedirectHandler.setContextPath("/");
			sslRedirectHandler.setHandler(new SecuredRedirectHandler());
			sslRedirectHandler.setVirtualHosts(new String[] {"@unsecure"});
			
			handlers.addHandler(sslRedirectHandler);
		}
		
		handlers.addHandler(this.servletContextHandlers);
		
		server.setHandler(handlers);
	}
	
	public boolean isSSLEnabled() {
		return this.conf.getSslPort() != 0;
	}

	@Override
	public void start() {
		LogManager.getLogger().info("Starting with " + this.servletContextHandlers.getHandlers().length + " ContextHandlers");
		
		try {
			this.server.start();
			this.server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		try {
			this.server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setIPAddress(String hostAddress) {
		for(Connector c : this.server.getConnectors()) {
			if(c instanceof ServerConnector) {
				ServerConnector sc = (ServerConnector) c;
				sc.setHost(hostAddress);
			}
		}
	}

	@Override
	public void setDomain(String domainName, String... additionalDomains) {
		throw new UnsupportedOperationException("Domain filtering is not implemented yet");
		//TODO Implement domain filtering using Filters
	}

	@Override
	public void setPort(int port) {
		for(Connector c : this.server.getConnectors()) {
			if(c instanceof ServerConnector) {
				ServerConnector sc = (ServerConnector) c;
				
				if(sc.getName().equals("secure")) {
					break;
				}
				
				sc.setPort(port);
			}
		}
	}

	@Override
	public void setSSLPort(int port) {
		for(Connector c : this.server.getConnectors()) {
			if(c instanceof ServerConnector) {
				ServerConnector sc = (ServerConnector) c;
				
				if(!sc.getName().equals("secure")) {
					break;
				}
				
				sc.setPort(port);
			}
		}
	}

	@Override
	public ApplicationContext getContext(Application app) throws IllegalArgumentException {
		return null;
	}
	
	public org.eclipse.jetty.server.Server getJettyServer() {
		return server;
	}

}
