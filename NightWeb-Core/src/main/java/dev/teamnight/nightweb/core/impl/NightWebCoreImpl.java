/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import dev.teamnight.nightweb.core.Application;
import dev.teamnight.nightweb.core.ApplicationContext;
import dev.teamnight.nightweb.core.NightModule;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.NightWebCore;
import dev.teamnight.nightweb.core.Server;
import dev.teamnight.nightweb.core.entities.ActivationType;
import dev.teamnight.nightweb.core.entities.ApplicationData;
import dev.teamnight.nightweb.core.entities.DefaultPermission;
import dev.teamnight.nightweb.core.entities.Group;
import dev.teamnight.nightweb.core.entities.GroupPermission;
import dev.teamnight.nightweb.core.entities.ModuleData;
import dev.teamnight.nightweb.core.entities.Permission.Tribool;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.entities.UserPermission;
import dev.teamnight.nightweb.core.entities.UserSetting;
import dev.teamnight.nightweb.core.entities.XmlConfiguration;
import dev.teamnight.nightweb.core.events.DefaultEventManagerImpl;
import dev.teamnight.nightweb.core.events.EventManager;
import dev.teamnight.nightweb.core.events.TemplatePreprocessEvent;
import dev.teamnight.nightweb.core.entities.Setting.Type;
import dev.teamnight.nightweb.core.entities.SystemSetting;
import dev.teamnight.nightweb.core.module.JavaModuleLoader;
import dev.teamnight.nightweb.core.module.ModuleManager;
import dev.teamnight.nightweb.core.module.ModuleManagerImpl;
import dev.teamnight.nightweb.core.service.ApplicationService;
import dev.teamnight.nightweb.core.service.ErrorLogService;
import dev.teamnight.nightweb.core.service.GroupService;
import dev.teamnight.nightweb.core.service.ModuleService;
import dev.teamnight.nightweb.core.service.PermissionService;
import dev.teamnight.nightweb.core.service.ServiceManager;
import dev.teamnight.nightweb.core.service.ServiceManagerImpl;
import dev.teamnight.nightweb.core.service.SettingService;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.servlets.ActivationServlet;
import dev.teamnight.nightweb.core.servlets.LoginServlet;
import dev.teamnight.nightweb.core.servlets.LogoutServlet;
import dev.teamnight.nightweb.core.servlets.RegistrationServlet;
import dev.teamnight.nightweb.core.servlets.TestServlet;
import dev.teamnight.nightweb.core.servlets.admin.AdminDashboardServlet;
import dev.teamnight.nightweb.core.servlets.admin.AdminLoginServlet;
import dev.teamnight.nightweb.core.servlets.admin.AdminModuleEditServlet;
import dev.teamnight.nightweb.core.servlets.admin.AdminModuleInstallListServlet;
import dev.teamnight.nightweb.core.servlets.admin.AdminModuleInstallServlet;
import dev.teamnight.nightweb.core.servlets.admin.AdminModuleListServlet;
import dev.teamnight.nightweb.core.servlets.admin.AdminModuleUninstallServlet;
import dev.teamnight.nightweb.core.servlets.admin.AdminSettingsServlet;
import dev.teamnight.nightweb.core.template.TemplateManager;
import dev.teamnight.nightweb.core.template.TemplateManagerImpl;
import freemarker.template.TemplateModelException;

public class NightWebCoreImpl extends Application implements NightWebCore {
	
	private static Logger LOGGER;

	private Server server;
	
	private SessionFactory sessionFactory;
	
	private ModuleManager moduleManager;
	
	private ServiceManager serviceMan;
	private UserService userService;
	private GroupService groupService;
	private PermissionService permService;
	
	private TemplateManager templateManager;
	private EventManager eventManager;

	private XmlConfiguration config;
	
	public NightWebCoreImpl() {
		//Initializing some things
		long start = System.currentTimeMillis();
		NightWeb.setCoreApplication(this);
		//TODO Implement public static main(NightWeb) by using modules from /modules or /libraries
		NightWebCoreImpl.LOGGER = LogManager.getLogger(getClass());
		
		//Needed paths for startup
		Path workingDir = NightWeb.WORKING_DIR;
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
		
		//Create default configuration file if not exists
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
		
		//Read config file
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
		
		//Set loggers to debug if debug is enabled
		if(this.config.isDebug()) {
			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
			LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
			loggerConfig.setLevel(Level.DEBUG);
			ctx.updateLoggers();
			LOGGER.debug(">>>>> Enabled debug messages for all loggers <<<<<");
		}
		
		//Check for hibernate config and copy it if it not exists
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
		
		//Create directories
		this.createIfNotExists(NightWeb.MODULES_DIR);
		this.createIfNotExists(NightWeb.STATIC_DIR);
		this.createIfNotExists(NightWeb.TEMPLATES_DIR);
		this.createIfNotExists(NightWeb.LANG_DIR);
		
		//Jetty Server
		this.server = new JettyServer(this, this.config);
		
		//Add core entities
		Configuration hibernateConf = new Configuration()
				.configure(hibernateConfigPath.toFile())
				.setProperty("hibernate.current_session_context_class", "org.hibernate.context.internal.ThreadLocalSessionContext")
				.addAnnotatedClass(ModuleData.class)
				.addAnnotatedClass(ApplicationData.class)
				.addAnnotatedClass(SystemSetting.class)
				.addAnnotatedClass(UserSetting.class)
				.addAnnotatedClass(DefaultPermission.class)
				.addAnnotatedClass(UserPermission.class)
				.addAnnotatedClass(GroupPermission.class)
				.addAnnotatedClass(User.class)
				.addAnnotatedClass(Group.class);
		
		//Event Manager
		this.eventManager = new DefaultEventManagerImpl();
		
		//Startup ModuleManager
		LOGGER.debug("Setting up ModuleManagerImpl");
		this.moduleManager = new ModuleManagerImpl(this);
		this.moduleManager.registerLoader(JavaModuleLoader.class);
		this.moduleManager.registerCustomizer(module -> {
			if(NightWebCoreImpl.this.sessionFactory != null) {
				throw new IllegalStateException("Module can not be loaded and configured after startup.");
			}
			
			List<Class<?>> classList = new ArrayList<Class<?>>();
			
			module.configure(classList);
			
			classList.forEach(clazz -> hibernateConf.addAnnotatedClass(clazz));
		});
		
		//Load modules from /modules dir
		//Module classes will be initialized but not called yet
		LOGGER.info("Loading modules...");
		this.moduleManager.loadModules(NightWeb.MODULES_DIR);
		
		//Build the sessionFactory before modules get enabled
		this.sessionFactory = hibernateConf.buildSessionFactory();
		this.server.setSessionFactory(this.sessionFactory);
		LOGGER.debug("Built SessionFactory: " + this.sessionFactory.getClass().getCanonicalName());
		
		//Register all important services
		LOGGER.info("Setting up core services...");
		this.serviceMan = new ServiceManagerImpl(this.sessionFactory);
		this.serviceMan.register(ApplicationService.class);
		this.serviceMan.register(ModuleService.class);
		this.serviceMan.register(SettingService.class);
		this.serviceMan.register(UserService.class);
		this.serviceMan.register(GroupService.class);
		this.serviceMan.register(PermissionService.class);
		this.serviceMan.register(ErrorLogService.class);
		
		//Retrieve the application data for the core application which is needed for default settings
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
		
		this.templateManager = new TemplateManagerImpl(NightWeb.TEMPLATES_DIR, NightWeb.LANG_DIR);
		
		//Doing setup things
		LOGGER.info("Creating default settings & permissions if not already existing...");
		this.createDefaultSettings(data.getModuleData());
		this.createDefaultPermissions(data.getModuleData());
		this.createDefaultGroups(data);
		this.registerEvents();
		
		//Set default language as shared variable for template function lang()
		try {
			this.templateManager.setSharedVariable("defaultLanguage", this.serviceMan.getService(SettingService.class).getByKey("defaultLanguage").getValue());
		} catch (TemplateModelException e) {
			e.printStackTrace();
		}
		
		this.setIdentifier("dev.teamnight.nightweb.core");
		ApplicationContext coreContext = this.server.getContext(this);
		this.init(coreContext);
		
		LOGGER.info("Enabling modules...");
		this.moduleManager.enableModules();
		
		LOGGER.info("Loaded up in " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + "s");
		
		LOGGER.info("Starting server");
		this.server.start();
	}

	private void createDefaultSettings(ModuleData data) {
		SettingService serv = this.serviceMan.getService(SettingService.class);
		
		//General settings
		SystemSetting pageTitle = new SystemSetting("pageTitle", "NightWeb", Type.STRING, "general", data);
		SystemSetting pageDesc = new SystemSetting("pageDescription", "", Type.STRING, "general", data);
		SystemSetting metaKeywords = new SystemSetting("metaKeywords", "", Type.STRING, "general", data);
		SystemSetting metaDesc = new SystemSetting("metaDescription", "", Type.STRING, "general", data);
		
		SystemSetting defaultLang = new SystemSetting("defaultLanguage", "en", Type.SELECTION, "general", new String[] {"en"}, data);
		
		//Developer
		SystemSetting dG = new SystemSetting("defaultGroup", "2", Type.NUMBER, "developer", data);
		SystemSetting gG = new SystemSetting("guestGroup", "1", Type.NUMBER, "developer", data);
		
		//User
		SystemSetting regEnabled = new SystemSetting("registrationEnabled", Boolean.TRUE.toString(), Type.FLAG, "user.register", data);
		SystemSetting loginEnabled = new SystemSetting("loginEnabled", Boolean.TRUE.toString(), Type.FLAG, "user", data);
		SystemSetting activationType = new SystemSetting("activationType", ActivationType.ACTIVATION.toString(), Type.RADIOBUTTON, "user.register", 
				Arrays.stream(ActivationType.class.getEnumConstants()).map(Enum::name).toArray(String[]::new), data);
		
		serv.create(pageTitle, pageDesc, metaKeywords, metaDesc, defaultLang, dG, gG, regEnabled, loginEnabled, activationType);
		
		SystemSetting defaultLangSET = serv.getByKey("defaultLanguage");
		LOGGER.debug("Null check: " + (this.templateManager.getLanguageManager().getAvailableLanguages() != null));
		LOGGER.debug("Null check: " + (defaultLangSET != null));
		defaultLangSET.setEnumValues(this.templateManager.getLanguageManager().getAvailableLanguages());
		
		serv.save(defaultLangSET);
	}
	
	private void createDefaultPermissions(ModuleData data) {
		DefaultPermission bypassDisabledLogin = new DefaultPermission("nightweb.admin.canBypassDisabledLogin", Tribool.NEUTRAL, data);
		DefaultPermission canUseACP = new DefaultPermission("nightweb.admin.canUseACP", Tribool.NEUTRAL, data);
		DefaultPermission canEditModules = new DefaultPermission("nightweb.admin.canManageModules", Tribool.NEUTRAL, data);
		DefaultPermission canInstallModules = new DefaultPermission("nightweb.admin.canInstallModules", Tribool.NEUTRAL, data);
		DefaultPermission canUninstallModules = new DefaultPermission("nightweb.admin.canUninstallModules", Tribool.NEUTRAL, data);
		DefaultPermission canManageSettings = new DefaultPermission("nightweb.admin.canManageSettings", Tribool.NEUTRAL, data);
		
		this.serviceMan.getService(PermissionService.class).create(bypassDisabledLogin, canUseACP, canEditModules, canInstallModules, canUninstallModules, canManageSettings);
	}
	
	private void createDefaultGroups(ApplicationData data) {
		//TODO change this to a setup routine or else
		Group guestGroup = new Group("Unregistered");
		Group registered = new Group("Registered");
		Group administrators = new Group("Administrators");
		
		List<DefaultPermission> permissions = this.serviceMan.getService(PermissionService.class).getAll();
		administrators.setPermissions(
				permissions.stream()
					.map(perm -> new GroupPermission(administrators, perm))
					.map(perm -> {
						perm.setValue(Tribool.TRUE.getAsString());
						
						return perm;
					})
					.collect(Collectors.toList())
				);
		
		administrators.setPriority(1000);
		administrators.setStaffGroup(true);
		
		this.serviceMan.getService(GroupService.class).create(guestGroup, registered, administrators);
	}
	

	/**
	 * 
	 */
	private void registerEvents() {
		this.eventManager.registerListener(TemplatePreprocessEvent.class, event -> {
			TemplatePreprocessEvent tmpEvent = (TemplatePreprocessEvent) event;
			
			SettingService setserv = this.serviceMan.getService(SettingService.class);
			
			SystemSetting pageTitle = setserv.getByKey("pageTitle");
			SystemSetting pageDesc = setserv.getByKey("pageDescription");
			SystemSetting metaKeywords = setserv.getByKey("metaKeywords");
			SystemSetting metaDesc = setserv.getByKey("metaDescription");
			
			tmpEvent.getTemplateBuilder().assign("title", pageTitle.getValue()).assign("pageDescription", pageDesc).assign("metaKeywords", metaKeywords).assign("metaDescription", metaDesc);
		});
	}

	/**
	 * @param directory
	 */
	private void createIfNotExists(Path directory) {
		if(!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
			LOGGER.info("Creating directory at" + directory);
			try {
				Files.createDirectory(directory);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	// ----------------------------------------------------------------------- //
	// Application                                                             //
	// ----------------------------------------------------------------------- //

	@Override
	public void configure(List<Class<?>> entityList) {
		//Ignore this, all core classes are getting loaded at startup in the constructor
	}

	@Override
	public void start(ApplicationContext ctx) {
		ServletHolder staticHolder = new ServletHolder("static", DefaultServlet.class);
		staticHolder.setInitParameter("resourceBase", NightWeb.STATIC_DIR.toAbsolutePath().toString());
		staticHolder.setInitParameter("dirAllowed", "false");
		staticHolder.setInitParameter("pathInfoOnly", "true");
		
		ctx.registerServletHolder(staticHolder, "/static/*");
		
		ctx.registerServlet(RegistrationServlet.class, "/register");
		ctx.registerServlet(ActivationServlet.class, "/activation/*");
		ctx.registerServlet(LoginServlet.class, "/login");
		ctx.registerServlet(LogoutServlet.class, "/logout");
		
		//Admin
		ctx.registerServlet(AdminDashboardServlet.class, "/admin");
		ctx.registerServlet(AdminLoginServlet.class, "/admin/login");
		ctx.registerServlet(AdminModuleListServlet.class, "/admin/modules/*");
		ctx.registerServlet(AdminModuleEditServlet.class, "/admin/module/*");
		ctx.registerServlet(AdminModuleInstallListServlet.class, "/admin/install-modules");
		ctx.registerServlet(AdminModuleInstallServlet.class, "/admin/install");
		ctx.registerServlet(AdminModuleUninstallServlet.class, "/admin/uninstall/*");
		ctx.registerServlet(AdminSettingsServlet.class, "/admin/settings/*");
		
		//Test
		ctx.registerServlet(TestServlet.class, "/test");
	}
	
	// ----------------------------------------------------------------------- //
	// NightWebCore                                                            //
	// ----------------------------------------------------------------------- //

	@Override
	public String getImplementationName() {
		return "NightWeb Core Standard";
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
		return "NightWeb";
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
	public TemplateManager getTemplateManager() {
		return this.templateManager;
	}
	
	@Override
	public EventManager getEventManager() {
		return this.eventManager;
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
	public List<HttpSession> getSessions() {
		return this.server.getSessions();
	}
	
	public ModuleManager getModuleManager() {
		return moduleManager;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	@Override
	public boolean isDebugModeEnabled() {
		return this.config.isDebug();
	}

	@Override
	public ModuleData getModuleData(String identifier) {
		return this.moduleManager.getData(identifier);
	}

	@Override
	public ModuleData getModuleData(NightModule module) {
		return this.moduleManager.getData(module.getIdentifier());
	}

}
