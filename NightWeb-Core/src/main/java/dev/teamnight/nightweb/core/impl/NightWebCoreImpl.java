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

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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

public class NightWebCoreImpl implements NightWebCore {

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
		
		Path workingDir = Paths.get(System.getProperty("user.dir"));
		Path modulesDir = workingDir.resolve("modules");
		Path configPath = workingDir.resolve("config.xml");
		Path hibernateConfigPath = workingDir.resolve("hibernate.cfg.xml");
		
		JAXBContext configContext;
		try {
			configContext = JAXBContext.newInstance(XmlConfiguration.class);
		} catch (JAXBException e) {
			e.printStackTrace();
			System.exit(1);
			return;
		}
		
		if(!Files.exists(configPath, LinkOption.NOFOLLOW_LINKS)) {
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
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			Unmarshaller unmarshaller = configContext.createUnmarshaller();
			this.config = (XmlConfiguration) unmarshaller.unmarshal(configPath.toAbsolutePath().toFile());
		} catch (JAXBException e) {
			e.printStackTrace();
			System.exit(1);
			return;
		}
		
		if(!Files.exists(hibernateConfigPath, LinkOption.NOFOLLOW_LINKS)) {
			try {
				Files.copy(this.getClass().getResourceAsStream("hibernate.cfg.xml"), hibernateConfigPath);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			System.exit(0);
			return;
		}
		
		if(!Files.exists(modulesDir, LinkOption.NOFOLLOW_LINKS)) {
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
		
		this.moduleManager.loadModules(modulesDir);
		
		this.sessionFactory = hibernateConf.buildSessionFactory();
		
		this.serviceMan = new ServiceManagerImpl(this.sessionFactory);
		this.serviceMan.register(ApplicationService.class);
		this.serviceMan.register(ModuleService.class);
		
		this.moduleManager.enableModules();
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

}
