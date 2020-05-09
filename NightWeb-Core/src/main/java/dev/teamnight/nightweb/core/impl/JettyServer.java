/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.NightWebCore;
import dev.teamnight.nightweb.core.Server;
import dev.teamnight.nightweb.core.entities.ApplicationData;
import dev.teamnight.nightweb.core.entities.XmlConfiguration;
import dev.teamnight.nightweb.core.service.ApplicationService;

public class JettyServer implements Server {

	private static final String RequestLogFormat = "%{client}a - %u \"%r\" %s %O \"%{Referer}i\" \"%{User-Agent}i\"";
	private static Logger LOGGER = LogManager.getLogger();
	
	private org.eclipse.jetty.server.Server server;
	
	private ContextHandlerCollection servletContextHandlers = new ContextHandlerCollection();
	
	private XmlConfiguration conf;
	
	private NightWebCore core;

	private SessionFactory sessionFactory;
	
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
			LOGGER.info("Using SSL supprto");
			HttpConfiguration httpsConf = new HttpConfiguration(httpConf);
			httpsConf.addCustomizer(new SecureRequestCustomizer());
			
			SslContextFactory sslContextFactory = new SslContextFactory.Server();
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
		server.setErrorHandler(new JettyErrorHandler(NightWeb.getTemplateManager()));
		server.setRequestLog(new CustomRequestLog(new Log4jRequestLogWriter(JettyServer.class), JettyServer.RequestLogFormat));
	}
	
	public boolean isSSLEnabled() {
		return this.conf.getSslPort() != 0;
	}

	@Override
	public void start() {
		LOGGER.info("Starting with "
				+ (this.servletContextHandlers.getHandlers() != null ? this.servletContextHandlers.getHandlers().length : 0)
				+ " ContextHandlers");
		
		try {
			this.server.start();
			
			LOGGER.info("Press CTRL+C to stop the server");
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					JettyServer.LOGGER.info("Shutting down...");
					
					JettyServer.this.stop();
					
					if(JettyServer.this.core instanceof NightWebCoreImpl) {
						NightWebCoreImpl impl = (NightWebCoreImpl) JettyServer.this.core;
						impl.getSessionFactory().close();
					}
				}
			});
			
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
				((ServerConnector) c).setHost(hostAddress);
			}
		}
	}

	@Override
	public void setDomain(String domainName, String... additionalDomains) {
		throw new UnsupportedOperationException("Domain filtering is not implemented yet");
		//TODO Implement domain filtering using Filters
	}

	@SuppressWarnings("resource")
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

	@SuppressWarnings("resource")
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
		if(app.getIdentifier() == null) {
			throw new IllegalArgumentException("Identifier for " + app.getClass().getCanonicalName() + " is null");
		}
		
		ApplicationService appService = this.core.getServiceManager().getService(ApplicationService.class);
		ApplicationData data = appService.getByIdentifier(app.getIdentifier());
		
		if(data == null) {
			throw new IllegalArgumentException("App " + app.getIdentifier() + " is not installed");
		}
		
		LOGGER.debug("Initialiazing ServletContextHandler with contextPath=" + data.getContextPath() + " for application " + data.getIdentifier());
		
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.setInitParameter("org.eclipse.jetty.servlet.SessionCookie", app.getIdentifier() + ".session");
		handler.setInitParameter("org.eclipse.jetty.servlet.SessionIdPathParameterName", "none");
		handler.setContextPath(data.getContextPath());
		
		ApplicationContext ctx = new JettyApplicationContext(handler, this.sessionFactory, this.core.getServiceManager(), this.core.getTemplateManager());
		ctx.setModule(app);
		
		this.servletContextHandlers.addHandler(handler);
		
		return ctx;
	}
	
	public org.eclipse.jetty.server.Server getJettyServer() {
		return server;
	}
	
	@Override
	public void setSessionFactory(SessionFactory factory) {
		this.sessionFactory = factory;
	}

}
