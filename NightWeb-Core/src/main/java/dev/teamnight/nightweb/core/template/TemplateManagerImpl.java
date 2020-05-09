/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.exceptions.TemplateProcessException;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Jonas
 *
 */
public class TemplateManagerImpl implements TemplateManager {

	private Configuration configuration;
	private Path templateDir;
	
	private LanguageManager languageManager;
	
	private Cache<String, Template> templateCache = CacheBuilder.newBuilder()
			.maximumSize(10000)
			.expireAfterAccess(3, TimeUnit.DAYS)
			.build();

	public TemplateManagerImpl(Path templateDir, Path languageDir) {
		try {
			this.languageManager = new LanguageManagerImpl();
			this.languageManager.loadLanguages(languageDir);
		} catch (JAXBException e) {
			this.languageManager = null;
			e.printStackTrace();
		}
		this.configuration = new Configuration(Configuration.VERSION_2_3_30);
		try {
			this.configuration.setDirectoryForTemplateLoading(templateDir.toAbsolutePath().toFile());
			this.configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
			this.configuration.setOutputEncoding(this.configuration.getDefaultEncoding());
			this.configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			this.configuration.setLogTemplateExceptions(true);
			this.configuration.setOutputFormat(HTMLOutputFormat.INSTANCE);
			
			this.configuration.setSharedVariable("domain", NightWeb.getCoreApplication().getDomain());
			this.configuration.setSharedVariable("languageManager", this.languageManager);
			
			Menu mainMenu = new Menu();
			mainMenu.setName("mainMenu");
			mainMenu.setActiveMenu("Home");
			mainMenu.addLeftItem(1, mainMenu.new Item("Home", "/"));
			mainMenu.addLeftItem(2, mainMenu.new Item("Articles", "/"));
			
			this.configuration.setSharedVariable("mainMenu", mainMenu);
		} catch (IOException | TemplateModelException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Configuration getConfiguration() {
		return this.configuration;
	}

	@Override
	public Path getTemplateDirectory() {
		return this.templateDir;
	}

	@Override
	public void setTemplateDirectory(Path path) throws IOException {
		this.templateDir = path;
		this.configuration.setDirectoryForTemplateLoading(path.toAbsolutePath().toFile());
	}

	@Override
	public TemplateBuilder builder(Template template) throws TemplateProcessException {
		try {
			return new TemplateBuilder(this, template);
		} catch (IOException e) {
			throw new TemplateProcessException(e);
		}
	}

	@Override
	public TemplateBuilder builder(String templatePath) throws TemplateProcessException {
		try {
			Template template = this.templateCache.get(templatePath, () -> {
				return this.configuration.getTemplate(templatePath);
			});
			
			return new TemplateBuilder(this, template);
		} catch (ExecutionException | IOException e) {
			throw new TemplateProcessException(e);
		}
	}
	
	@Override
	public TemplateBuilder builder(String templatePath, Context ctx) throws TemplateProcessException {
		try {
			Template template = this.templateCache.get(templatePath, () -> {
				return this.configuration.getTemplate(templatePath);
			});
			
			return new TemplateBuilder(this, template).assign("contextPath", ctx.getContextPath());
		} catch (ExecutionException | IOException e) {
			throw new TemplateProcessException(e);
		}
	}

	@Override
	public Template getTemplate(String templatePath) throws TemplateProcessException {
		try {
			return this.templateCache.get(templatePath, () -> {
				return this.configuration.getTemplate(templatePath);
			});
		} catch (ExecutionException e) {
			NightWeb.logError(e, this.getClass());
			throw new TemplateProcessException(e);
		}
	}

	@Override
	public void loadAllTemplates() {
		try {
			Files.list(templateDir).forEach(path -> {
				if(Files.isDirectory(path)) {
					try {
						this.loadTemplatesInDirectory(path);
					} catch (IOException e) {
						NightWeb.logError(e, this.getClass());
						e.printStackTrace();
					}
				}
				
				String templatePath = this.templateDir.relativize(path).toString();
				
				try {
					this.templateCache.get(templatePath, () -> {
						return this.configuration.getTemplate(templatePath);
					});
				} catch (ExecutionException e) {
					NightWeb.logError(e, this.getClass());
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadTemplatesInDirectory(Path path) throws IOException {
		Files.list(path).forEach(listPath -> {
			if(Files.isDirectory(listPath)) {
				try {
					this.loadTemplatesInDirectory(listPath);
					return;
				} catch (IOException e) {
					NightWeb.logError(e, this.getClass());
					e.printStackTrace();
				}
			}
			
			String templatePath = this.templateDir.relativize(listPath).toString();
			
			try {
				this.templateCache.get(templatePath, () -> {
					return this.configuration.getTemplate(templatePath);
				});
			} catch (ExecutionException e) {
				NightWeb.logError(e, this.getClass());
				e.printStackTrace();
			}
		});
	}

	@Override
	public void loadTemplate(String templatePath) {
		try {
			this.templateCache.get(templatePath, () -> {
				return this.configuration.getTemplate(templatePath);
			});
		} catch (ExecutionException e) {
			NightWeb.logError(e, this.getClass());
			e.printStackTrace();
		}
	}

	@Override
	public List<Template> getCachedTemplates() {
		List<Template> templates = new ArrayList<Template>();
		
		for(Template temp : this.templateCache.asMap().values()) {
			templates.add(temp);
		}
		
		return templates;
	}

	@Override
	public void clearTemplateCache() {
		this.templateCache.invalidateAll();
	}
	
	@Override
	public TemplateModel getSharedVariable(String key) {
		return this.configuration.getSharedVariable(key);
	}
	
	@Override
	public void setSharedVariable(String key, Object value) throws TemplateModelException {
		this.configuration.setSharedVariable(key, value);
	}

	@Override
	public LanguageManager getLanguageManager() {
		return this.languageManager;
	}

}
