/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl.jetty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.hibernate.SessionFactory;

import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.NightWebCore;
import dev.teamnight.nightweb.core.Server;
import dev.teamnight.nightweb.core.ServletRegistrationAdapter;
import dev.teamnight.nightweb.core.entities.XmlConfiguration;
import dev.teamnight.nightweb.core.events.ServerStartedEvent;
import dev.teamnight.nightweb.core.impl.NightWebCoreImpl;
import dev.teamnight.nightweb.core.mvc.Router;
import dev.teamnight.nightweb.core.util.StringUtil;

public class JettyServer implements Server, HttpSessionListener, HttpSessionIdListener {

	private static final String RequestLogFormat = "%{client}a - %u \"%r\" %s %O \"%{Referer}i\" \"%{User-Agent}i\"";
	private static Logger LOGGER = LogManager.getLogger();
	
	private org.eclipse.jetty.server.Server server;
	private List<String> sessionIds = new ArrayList<String>();
	private ReentrantLock sessionIdsLock = new ReentrantLock();
	
	private ContextHandlerCollection servletContextHandlers = new ContextHandlerCollection();
	private Map<ApplicationContext, JettyServletRegistrationAdapter> servletRegistrationAdapters = new HashMap<ApplicationContext, JettyServletRegistrationAdapter>();
	
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
			LOGGER.info("Using SSL support");
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
		
		HandlerCollection handlingProcessCollection = new HandlerCollection();
		handlingProcessCollection.addHandler(this.servletContextHandlers);
		handlingProcessCollection.addHandler(new PostHandler());
		
		handlers.addHandler(handlingProcessCollection);
		
		server.setHandler(handlers);
		server.setRequestLog(new CustomRequestLog(new Log4jRequestLogWriter(JettyServer.class), JettyServer.RequestLogFormat));
		server.setSessionIdManager(new DefaultSessionIdManager(server));
	}
	
	public boolean isSSLEnabled() {
		return this.conf.getSslPort() != 0;
	}

	@Override
	public void start() {
		LOGGER.info("Starting with "
				+ (this.servletContextHandlers.getHandlers() != null ? this.servletContextHandlers.getHandlers().length : 0)
				+ " ContextHandlers");
		
		this.server.setErrorHandler(new JettyErrorHandler(NightWeb.getTemplateManager()));
		
		try {
			this.server.start();
			this.servletRegistrationAdapters.values().forEach(adapter -> adapter.updatePathRegistry());
			
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
			
			this.core.getEventManager().fireEvent(new ServerStartedEvent());
			
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
	public ServletRegistrationAdapter getServletRegistration(ApplicationContext appContext)
			throws IllegalArgumentException {
		if(appContext.getModuleIdentifier() == null) {
			throw new IllegalArgumentException("Identifier for " + appContext + " is null");
		}
		
		LOGGER.debug("Initialiazing ServletContextHandler with contextPath=" + appContext.getContextPath() + " for application " + appContext.getModuleIdentifier());
		
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		handler.setInitParameter("org.eclipse.jetty.servlet.SessionCookie", "dev.teamnight.nightweb.core.session");
		handler.setInitParameter("org.eclipse.jetty.servlet.SessionIdPathParameterName", "none");
		handler.setContextPath(appContext.getContextPath());
		handler.setErrorHandler(this.server.getErrorHandler());
		handler.getServletHandler().setServletMappings(new ServletMapping[] {}); //to fix a bug in getServletURL
		
		SessionHandler sessions = new SessionHandler();
		sessions.setServer(this.server);
		sessions.setSessionIdManager(this.server.getSessionIdManager());
		sessions.addEventListener(this);
		handler.setSessionHandler(sessions);
		
		JettyServletRegistrationAdapter adapter = new JettyServletRegistrationAdapter(handler);
		adapter.setPathRegistry(this.core.getPathRegistry());
		
		this.servletRegistrationAdapters.put(appContext, adapter);
		
		return adapter;
	}
	
	@Override
	public String getServletURL(Class<? extends HttpServlet> servletClass) {
		return this.getServletURL(servletClass.getName());
	}
	
	@Override
	public String getServletURL(String servletClass) {
		String url = null;
		
		for(Handler handler : this.servletContextHandlers.getHandlers()) {
			if(!(handler instanceof ServletContextHandler)) {
				throw new IllegalArgumentException("Unallowed handler in ContextHandlerCollection");
			}
			ServletContextHandler ctxHandler = (ServletContextHandler) handler;
			
			//Check servlets
			for(ServletMapping map : ctxHandler.getServletHandler().getServletMappings()) {
				if(map.getServletName().startsWith(servletClass)) {
					url = map.getPathSpecs()[0].replace("/*", "");
					
					url = StringUtil.filterURL(ctxHandler.getContextPath() + url);
				}
			}
			
			//Check Router
			if(url == null) {
				ServletHolder holder = ctxHandler.getServletHandler().getServlet("Router");
				
				if(holder == null ||
						holder.getServletInstance() == null) {
					return null;
				}
				
				if(holder.getServletInstance() instanceof Router) {
					Router router = (Router) holder.getServletInstance();
					
					return router.getPath(servletClass);
				}
			}
		}
		
		return url;
	}
	
	public org.eclipse.jetty.server.Server getJettyServer() {
		return server;
	}
	
	@Override
	public void setSessionFactory(SessionFactory factory) {
		this.sessionFactory = factory;
	}

	@Override
	public List<HttpSession> getSessions() {
		//TODO: same SessionCache for all contexts
		return this.sessionIds.stream().map(
				id -> this.server.getSessionIdManager().getSessionHandlers().stream()
						.map(handler -> handler.getSession(id))
						.filter(session -> session != null)
						.findFirst()
						.orElse(null)
				).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
		boolean locked = false;
		try {
			locked = this.sessionIdsLock.tryLock(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(!locked) {
			throw new IllegalStateException("Unable to change session id in list within 2 seconds.");
		}
		
		this.sessionIds.remove(oldSessionId);
		this.sessionIds.add(event.getSession().getId());
		
		this.sessionIdsLock.unlock();
	}

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		boolean locked = false;
		try {
			locked = this.sessionIdsLock.tryLock(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(!locked) {
			throw new IllegalStateException("Unable to change session id in list within 2 seconds.");
		}
		
		this.sessionIds.add(se.getSession().getId());
		this.sessionIdsLock.unlock();
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		boolean locked = false;
		try {
			locked = this.sessionIdsLock.tryLock(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(!locked) {
			throw new IllegalStateException("Unable to change session id in list within 2 seconds.");
		}
		
		this.sessionIds.remove(se.getSession().getId());
		this.sessionIdsLock.unlock();
	}

}
