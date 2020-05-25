/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import javax.xml.bind.JAXBException;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.events.TemplatePreprocessEvent;
import dev.teamnight.nightweb.core.exceptions.TemplateProcessException;
import freemarker.cache.MruCacheStorage;
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
			this.configuration.setCacheStorage(new MruCacheStorage(25, Integer.MAX_VALUE));
			
			this.configuration.setSharedVariable("domain", NightWeb.getCoreApplication().getDomain());
			this.configuration.setSharedVariable("languageManager", this.languageManager);
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
			TemplateBuilder builder = new TemplateBuilder(this, template);
			
			//Fire the event
			NightWeb.getEventManager().fireEvent(new TemplatePreprocessEvent(builder, null));
			
			return builder;
		} catch (IOException e) {
			throw new TemplateProcessException(e);
		}
	}

	@Override
	public TemplateBuilder builder(String templatePath) throws TemplateProcessException {
		try {
			Template template = this.configuration.getTemplate(templatePath);
			
			TemplateBuilder builder = new TemplateBuilder(this, template);
			
			//Fire the event
			NightWeb.getEventManager().fireEvent(new TemplatePreprocessEvent(builder, null));
			
			return builder;
		} catch (IOException e) {
			throw new TemplateProcessException(e);
		}
	}
	
	@Override
	public TemplateBuilder builder(String templatePath, Context ctx) throws TemplateProcessException {
		try {
			Template template = this.configuration.getTemplate(templatePath);
			
			TemplateBuilder builder = new TemplateBuilder(this, template).assign("contextPath", ctx.getContextPath());
			
			//Fire the event
			NightWeb.getEventManager().fireEvent(new TemplatePreprocessEvent(builder, ctx));
			
			return builder;
		} catch (IOException e) {
			throw new TemplateProcessException(e);
		}
	}

	@Override
	public Template getTemplate(String templatePath) throws TemplateProcessException {
		try {
			return this.configuration.getTemplate(templatePath);
		} catch (IOException e) {
			throw new TemplateProcessException(e);
		}
	}

	@Override
	public void clearTemplateCache() {
		this.configuration.clearTemplateCache();
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
