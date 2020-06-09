/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;

import dev.teamnight.nightweb.core.exceptions.TemplateProcessException;
import dev.teamnight.nightweb.core.template.AlertMessage.Type;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * @author Jonas
 *
 */
public class TemplateBuilder {

	private Template temp = null;
	
	private TemplateManager tmpMan;
	private HashMap<String, Object> replacements = new HashMap<String, Object>();
	
	public TemplateBuilder(TemplateManager templateManager, Template template) 
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
		this.tmpMan = templateManager;
		this.temp = template;
	}
	
	/**
	 * @return the template Manager
	 */
	public TemplateManager getTemplateManager() {
		return tmpMan;
	}
	
	/**
	 * Assigns a java object to a variable in the freemarker template
	 * @param String the variable name
	 * @param Object the object to be assigned
	 * @return the TemplateBuilder instance
	 */
	public TemplateBuilder assign(String var, Object obj) {
		this.replacements.put(var, obj);
		
		return this;
	}
	
	/**
	 * Assigns a java object to a variable in the freemarker template depending that the condition resolves to true
	 * @param String the variable name
	 * @param Object the object to be assigned
	 * @param Boolean the condition
	 * @return the TemplateBuilder instance
	 */
	public TemplateBuilder assignWithCond(String var, Object obj, boolean condition) {
		if(condition)
			this.assign(var, obj);
		
		return this;
	}
	
	/**
	 * Removes a variable from the assignments
	 * @param var
	 * @return
	 */
	public TemplateBuilder remove(String var) {
		this.replacements.remove(var);
		
		return this;
	}
	
	/**
	 * Assigns a value with the name message containing an {@link dev.teamnight.nightweb.core.template.AlertMessage} object of error type
	 * @param msg
	 * @return the TemplateBuilder
	 */
	public TemplateBuilder withErrorMessage(String msg) {
		return this.assign("message", new AlertMessage(msg, Type.ERROR));
	}
	
	/**
	 * Assigns a value with the name message containing an {@link dev.teamnight.nightweb.core.template.AlertMessage} object of success type
	 * @param msg
	 * @return the TemplateBuilder
	 */
	public TemplateBuilder withSuccessMessage(String msg) {
		return this.assign("message", new AlertMessage(msg, Type.SUCCESS));
	}
	
	/**
	 * @return the Template
	 */
	public Template getTemplate() {
		return this.temp;
	}
	
	/**
	 * Builds the template
	 * @return String the processed Template
	 * @throws TemplateProcessException if an {@link IOException} or {@link TemplateException} occurs
	 */
	public String build() throws TemplateProcessException {
		if(this.temp != null) {
			Template temp = this.getTemplate();
			StringWriter out = new StringWriter();
			
			this.replacements.forEach((key, replacement) -> LogManager.getLogger().debug(key + ":" + replacement));
			
			try {
				temp.process(this.replacements, out);
			} catch (TemplateException e) {
				throw new TemplateProcessException(e);
			} catch (IOException e) {
				throw new TemplateProcessException(e);
			}
			
			return out.toString();
		} else {
			return null;
		}
	}
	
	/**
	 * Builds the template and writes it to the HttpServletResposne
	 * @param HttpServletResponse the Response
	 * @throws TemplateProcessException @see {@link TemplateBuilder#build()}
	 * @throws IOException if the response could not be written to the response
	 */
	public void send(HttpServletResponse resp) throws TemplateProcessException, IOException {
		resp.getWriter().write(this.build());
	}
}
