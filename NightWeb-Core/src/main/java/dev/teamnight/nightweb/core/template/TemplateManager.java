/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

import java.io.IOException;
import java.nio.file.Path;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.exceptions.TemplateProcessException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author Jonas
 *
 */
public interface TemplateManager {
	
	public LanguageManager getLanguageManager();
	
	public Configuration getConfiguration();
	
	public Path getTemplateDirectory();
	
	public void setTemplateDirectory(Path path) throws IOException;

	public TemplateBuilder builder(Template template) throws TemplateProcessException;
	
	public TemplateBuilder builder(String templatePath) throws TemplateProcessException;
	
	public TemplateBuilder builder(String templatePath, Context ctx) throws TemplateProcessException;
	
	public Template getTemplate(String templatePath);
	
	public void clearTemplateCache();
	
	public void setSharedVariable(String key, Object value) throws TemplateModelException;
	
	public TemplateModel getSharedVariable(String key);
	
}
