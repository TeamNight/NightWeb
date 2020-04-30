/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;
import java.lang.module.ModuleDescriptor.Opens;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.NightWebCore;
import dev.teamnight.nightweb.core.Server;
import dev.teamnight.nightweb.core.entities.ApplicationData;
import dev.teamnight.nightweb.core.entities.ModuleData;
import dev.teamnight.nightweb.core.entities.XmlConfiguration;
import dev.teamnight.nightweb.core.module.JavaModuleLoader;
import dev.teamnight.nightweb.core.module.ModuleManager;
import dev.teamnight.nightweb.core.module.ModuleManagerImpl;
import dev.teamnight.nightweb.core.service.ApplicationService;
import dev.teamnight.nightweb.core.service.GroupService;
import dev.teamnight.nightweb.core.service.ModuleService;
import dev.teamnight.nightweb.core.service.PermissionService;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.service.ServiceManagerImpl;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.servlets.TestServlet;

public class NightWebCoreImpl extends Application implements NightWebCore {

	public static final Path WORKING_DIR = Paths.get(System.getProperty("user.dir"));
	
	private static Logger LOGGER;

	private Server server;
	
	private SessionFactory sessionFactory;
	
	private ModuleManager moduleManager;
	
	private ServiceManager serviceMan;
	private UserService userService;
	private GroupService groupService;
	private PermissionService permService;

	private XmlConfiguration config;
	
	public NightWebCoreImpl() {
		//TODO Implement public static main(NightWeb) by using modules from /modules or /libraries
		NightWebCoreImpl.LOGGER = LogManager.getLogger(getClass());
		
		Path workingDir = NightWebCoreImpl.WORKING_DIR;
		Path modulesDir = workingDir.resolve("modules");
		Path configPath = workingDir.resolve("config.xml");
		Path hibernateConfigPath = workingDir.resolve("hibernate.cfg.xml");
		
		JAXBContext configContext;
		try {
			configContext = JAXBContext.newInstance(XmlConfiguration.class);
		} catch (JAXBException e) {
			LOGGER.error("Unable to create JAXBContext for configuration file: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
			return;
		}
		
		if(!Files.exists(configPath, LinkOption.NOFOLLOW_LINKS)) {
			LOGGER.info("Configuration is not existing, creating one...");
			XmlConfiguration defaultConfig = new XmlConfiguration();
			defaultConfig.setDebug(false);
			defaultConfig.setDomain("localhost");
			defaultConfig.setHostIPAddress("localhost");
			defaultConfig.setPort(8080);
			defaultConfig.setSslPort(8443);
			defaultConfig.setKeystoreFile(workingDir.resolve("keystore.jks").toAbsolutePath().toString());
			defaultConfig.setKeystorePassword("root");
			defaultConfig.setMinThreads(10);
			defaultConfig.setMaxThreads(100);
			
			try {
				Marshaller marshaller = configContext.createMarshaller();
				marshaller.marshal(defaultConfig, configPath.toAbsolutePath().toFile());
				LOGGER.info("Created configuration file, please edit it now.");
				System.exit(0);
			} catch (JAXBException e) {
				LOGGER.error("Unable to create configuration file: " + e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		try {
			Unmarshaller unmarshaller = configContext.createUnmarshaller();
			this.config = (XmlConfiguration) unmarshaller.unmarshal(configPath.toAbsolutePath().toFile());
			LOGGER.info("Loaded configuration file: " + configPath);
		} catch (JAXBException e) {
			LOGGER.error("Unable to load configuration file: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
			return;
		}
		
		if(this.config.isDebug()) {
			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
			LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
			loggerConfig.setLevel(Level.DEBUG);
			ctx.updateLoggers();
			LOGGER.debug(">>>>> Enabled debug messages for all loggers <<<<<");
		}
		
		if(!Files.exists(hibernateConfigPath, LinkOption.NOFOLLOW_LINKS)) {
			LOGGER.info("Database configuration does not exist, creating one...");
			try {
				Files.copy(this.getClass().getResourceAsStream("/hibernate.cfg.xml"), hibernateConfigPath);
			} catch (IOException e) {
				LOGGER.error("Unable to copy default configuration file from jar, please create one.");
				e.printStackTrace();
				System.exit(1);
			}
			
			LOGGER.info("Created database configuration file, please edit it now.");
			System.exit(0);
			return;
		}
		
		if(!Files.exists(modulesDir, LinkOption.NOFOLLOW_LINKS)) {
			LOGGER.info("Creating modules directory at" + modulesDir);
			try {
				Files.createDirectory(modulesDir);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		this.server = new JettyServer(this, this.config);
		
		Configuration hibernateConf = new Configuration()
				.configure(hibernateConfigPath.toFile())
				.addAnnotatedClass(ModuleData.class)
				.addAnnotatedClass(ApplicationData.class);
		
		LOGGER.debug("Setting up ModuleManagerImpl");
		this.moduleManager = new ModuleManagerImpl(this);
		this.moduleManager.registerLoader(JavaModuleLoader.class);
//		this.moduleManager.registerCustomizer((module) -> {
//			Module mod = module.getClass().getModule();
//			
//			ModuleReference ref = mod.getLayer().configuration().modules().stream()
//					.filter(rm -> rm.name().equals(mod.getName()))
//					.map(ResolvedModule::reference)
//					.findFirst()
//					.get();
//			
//			Set<Opens> opens = mod.getDescriptor().opens();
//			
//			try {
//				ModuleReader mr = ref.open();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			//TODO Do the Hibernate Conf thing
//		});
		this.moduleManager.registerCustomizer(module -> {
			List<Class<?>> classList = new ArrayList<Class<?>>();
			
			module.configure(classList);
			
			classList.forEach(clazz -> hibernateConf.addAnnotatedClass(clazz));
		});
		
		LOGGER.info("Loading modules...");
		this.moduleManager.loadModules(modulesDir);
		
		this.sessionFactory = hibernateConf.buildSessionFactory();
		this.server.setSessionFactory(this.sessionFactory);
		LOGGER.debug("Built SessionFactory: " + this.sessionFactory.getClass().getCanonicalName());
		
		LOGGER.info("Setting up services...");
		this.serviceMan = new ServiceManagerImpl(this.sessionFactory);
		this.serviceMan.register(ApplicationService.class);
		this.serviceMan.register(ModuleService.class);
		
		LOGGER.info("Enabling core application...");
		ApplicationService appService = this.serviceMan.getService(ApplicationService.class);
		ApplicationData data = appService.getByIdentifier("dev.teamnight.nightweb.core");
		
		if(data == null) {
			data = new ApplicationData();
			data.setName("NightWeb Core");
			data.setIdentifier("dev.teamnight.nightweb.core");
			data.setVersion("1.0.0");
			data.setContextPath("/");
			data.setModuleData(new ModuleData(data));
			
			appService.save(data);
		}
		this.setIdentifier("dev.teamnight.nightweb.core");
		ApplicationContext coreContext = this.server.getContext(this);
		this.init(coreContext);
		
		LOGGER.info("Enabling modules...");
		this.moduleManager.enableModules();
		
		LOGGER.info("Starting server");
		this.server.start();
	}
	
	@Override
	public String getImplementationName() {
		return "Standard";
	}

	@Override
	public String getVersion() {
		return "1.0.0-SNAPSHOT";
	}

	@Override
	public String getIPAddress() {
		return null;
	}

	@Override
	public String getDomain() {
		return null;
	}

	@Override
	public int getPort() {
		return 0;
	}

	@Override
	public Server getServer() {
		return this.server;
	}

	@Override
	public void setServer(Server server) throws IllegalArgumentException {
		if(this.server != null) {
			throw new IllegalArgumentException("Server can only be set once during startup.");
		}
		
		this.server = server;
	}

	@Override
	public ServiceManager getServiceManager() {
		return this.serviceMan;
	}

	@Override
	public String getTemplateManager() {
		return null;
	}

	@Override
	public String getMailService() {
		return null;
	}

	@Override
	public UserService getUserService() {
		return this.userService;
	}

	@Override
	public GroupService getGroupService() {
		return this.groupService;
	}

	@Override
	public PermissionService getPermissionService() {
		return this.permService;
	}

	@Override
	public String getSessions() {
		return null;
	}
	
	public ModuleManager getModuleManager() {
		return moduleManager;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public void configure(List<Class<?>> entityList) {
		//Ignore this, all core classes are getting loaded at startup in the constructor
	}

	@Override
	public void init(ApplicationContext ctx) {
		ctx.registerServlet(TestServlet.class, "/*");
	}

}
