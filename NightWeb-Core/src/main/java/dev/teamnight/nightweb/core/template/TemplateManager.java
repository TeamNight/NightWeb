/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

/**
 * @author Jonas
 *
 */
public interface TemplateManager {
	
	public Configuration getConfiguration();
	
	public Path getTemplateDirectory();
	
	public void setTemplateDirectory(Path path) throws IOException;

	public TemplateBuilder builder(Template template) throws TemplateProcessException;
	
	public TemplateBuilder builder(String templatePath) throws TemplateProcessException;
	
	public Template getTemplate(String templatePath);
	
	public void loadAllTemplates();
	
	public void loadTemplate(String templatePath);
	
	public List<Template> getCachedTemplates();
	
	public void clearTemplateCache();
	
}
