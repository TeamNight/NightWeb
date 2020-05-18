/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.LongValidator;
import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.PathParameters;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.Setting;
import dev.teamnight.nightweb.core.entities.SystemSetting;
import dev.teamnight.nightweb.core.service.SettingService;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminSettingsServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canManageSettings")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		SettingService setserv = ctx.getServiceManager().getService(SettingService.class);
		
		String categoryName = new PathParameters(req.getPathInfo()).getParameter(0);
		
		Set<String> categoriesWithSub = setserv.getCategories();
		
		Set<String> categories = categoriesWithSub.stream().filter(string -> !string.contains(".")).collect(Collectors.toSet());
		
		if(categoryName == null) {
			ctx.getTemplate("admin/settings.tpl")
				.assign("currentUser", auth.getUser())
				.assign("categories", categories)
				.send(resp);
			return;
		}
		
		List<SystemSetting> settings = setserv.getByCategory(categoryName);
		
		Multimap<String, Setting> settingNodes = MultimapBuilder.hashKeys().arrayListValues().build();
		
		for(Setting setting : settings) {
			String category = setting.getCategory();
			
			if(category.contains(".")) {
				category = category.split("\\.")[1];
			} else {
				category = "";
			}
			
			settingNodes.put(category, setting);
		}
		
		ctx.getTemplate("admin/settings.tpl")
			.assign("currentUser", auth.getUser())
			.assign("categories", categories)
			.assign("activeCategory", categoryName)
			.assign("settings", settingNodes)
			.assign("saved", (req.getParameter("saved") != null ? "true" : "false"))
			.send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canManageSettings")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		SettingService setserv = ctx.getServiceManager().getService(SettingService.class);
		
		String categoryName = new PathParameters(req.getPathInfo()).getParameter(0);
		
		Set<String> categoriesWithSub = setserv.getCategories();
		
		Set<String> categories = categoriesWithSub.stream().filter(string -> !string.contains(".")).collect(Collectors.toSet());
		
		if(categoryName == null) {
			ctx.getTemplate("admin/settings.tpl")
				.assign("currentUser", auth.getUser())
				.assign("categories", categories)
				.send(resp);
			return;
		}
		
		List<SystemSetting> settings = setserv.getByCategory(categoryName);
		
		for(SystemSetting setting : settings) {
			String value = req.getParameter("setting-" + setting.getKey());
			
			if(value == null) {
				continue;
			}
			
			LongValidator longVal = LongValidator.getInstance();
			
			LogManager.getLogger().debug("Settings old Value: " + setting.getValue());
			
			switch(setting.getType()) {
				case STRING:
					setting.setValue(value);
					break;
				case NUMBER:
					if(longVal.isValid(value)) {
						setting.setValue(value);
					} else {
						ctx.getTemplate("admin/settings.tpl")
							.assign("currentUser", auth.getUser())
							.assign("categories", categories)
							.assign("failed", "illegalNumberError")
							.assign("errorReason", setting.getKey())
							.send(resp);
						return;
					}
					break;
				case FLAG:
					if(value.equalsIgnoreCase("true")) {
						setting.setValue(Boolean.TRUE.toString());
					} else {
						setting.setValue(Boolean.FALSE.toString());
					}
					break;
				case RADIOBUTTON:
				case SELECTION:
					String[] possibleValues = setting.getEnumValues();
					
					if(possibleValues.length == 0) {
						break; //Break if the setting is not properly configured by the module providing it
					}
					
					for(String possibleValue : possibleValues) {
						if(value.equalsIgnoreCase(possibleValue)) {
							setting.setValue(possibleValue);
						}
					}
					break;
				case HIDDEN:
					break;
			}
			
			LogManager.getLogger().debug("Settings new Value: " + setting.getValue());
			
			setserv.save(setting);
			
			resp.sendRedirect(req.getRequestURI() + "?saved");
		}
	}

}
