/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets.admin;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.LongValidator;
import org.apache.logging.log4j.LogManager;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.annotations.AdminServlet;
import dev.teamnight.nightweb.core.entities.DefaultPermission;
import dev.teamnight.nightweb.core.entities.Permission;
import dev.teamnight.nightweb.core.entities.Permission.Tribool;
import dev.teamnight.nightweb.core.entities.PermissionCategory;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.entities.UserPermission;
import dev.teamnight.nightweb.core.service.PermissionService;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.util.NodeMap;
import dev.teamnight.nightweb.core.util.PathParameters;

/**
 * @author Jonas
 *
 */
@AdminServlet
public class AdminUserPermissionsEditServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canEditPermissions")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		String sUserId = new PathParameters(req.getPathInfo()).getParameter(0);
		
		long userId = 0L;
		
		LongValidator val = LongValidator.getInstance();
		if(val.isValid(sUserId)) {
			userId = Long.parseLong(sUserId);
		}
		
		if(userId == 0L) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownUserError")
				.send(resp);
			return;
		}
		
		UserService userv = ctx.getServiceManager().getService(UserService.class);
		User user = userv.getOne(userId);
		
		if(user == null) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownUserError")
				.send(resp);
			return;
		}
		
		if(!auth.getUser().canEdit(user) && auth.getUser().getId() != user.getId()) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "insufficientPermissionError")
				.send(resp);
			return;
		}
		
		long beforeDBtime = System.currentTimeMillis();
		
		PermissionService permServ = ctx.getServiceManager().getService(PermissionService.class);
		List<DefaultPermission> permissions = permServ.getAll();
		List<PermissionCategory> categories = permServ.getCategories();
		
		long startTime = System.currentTimeMillis();
		
		NodeMap<String, DefaultPermission> map = new NodeMap<String, DefaultPermission>();
		categories.stream()
			.filter(cat -> cat.getParent() == null)
			.sorted()
			.forEach(cat -> map.addTopNode(cat.getName()));
		
		LogManager.getLogger().debug("NodeMap: " + map.sizeTopNodes());
		
		for(NodeMap<String, DefaultPermission>.Node topNode : map.getTopNodes()) {
			List<PermissionCategory> secondLevelCategories = categories.stream()
															.filter(cat -> cat.getParent() != null)
															.filter(cat -> cat.getParent().getName() == topNode.getKey())
															.sorted()
															.collect(Collectors.toList());
			
			for(PermissionCategory secondCategory : secondLevelCategories) {
				NodeMap<String, DefaultPermission>.Node node = map.new Node(secondCategory.getName(), topNode);
				topNode.getSubNodes().add(node);
				
				permissions.stream()
					.filter(perm -> perm.getCategory().getId() == secondCategory.getId())
					.sorted()
					.forEach(perm -> node.addValue(perm));
				
				List<PermissionCategory> thirdLevelCategories = categories.stream()
																.filter(cat -> cat.getParent() != null)
																.filter(cat -> cat.getParent().getId() == secondCategory.getId())
																.sorted()
																.collect(Collectors.toList());
				
				for(PermissionCategory thirdLevelCategory : thirdLevelCategories) {
					NodeMap<String, DefaultPermission>.Node thirdNode = map.new Node(thirdLevelCategory.getName(), node);
					node.getSubNodes().add(thirdNode);
					
					permissions.stream()
						.filter(perm -> perm.getCategory().getId() == thirdLevelCategory.getId())
						.sorted()
						.forEach(perm -> thirdNode.addValue(perm));
					
					List<PermissionCategory> fourthLevelCategories = categories.stream()
																	.filter(cat -> cat.getParent() != null)
																	.filter(cat -> cat.getParent().getId() == thirdLevelCategory.getId())
																	.sorted()
																	.collect(Collectors.toList());
					
					if(fourthLevelCategories.size() > 0) {
						for(PermissionCategory fourthLevelCategory : fourthLevelCategories) {
							NodeMap<String, DefaultPermission>.Node fourthNode = map.new Node(fourthLevelCategory.getName(), node);
							thirdNode.getSubNodes().add(fourthNode);
							
							permissions.stream()
								.filter(perm -> perm.getCategory().getId() == fourthLevelCategory.getId())
								.sorted()
								.forEach(perm -> fourthNode.addValue(perm));
						}
					}
				}
			}
		}
		
		long endTime = System.currentTimeMillis();
		LogManager.getLogger().debug("NodeMap population costed " + (endTime - startTime) + "ms");
		LogManager.getLogger().debug("NodeMap population (+DB) costed " + (endTime - beforeDBtime) + "ms");
		
		ctx.getTemplate("admin/userPermissionEdit.tpl")
			.assign("currentUser", auth.getUser())
			.assign("user", user)
			.assign("permissions", map)
			.assign("setPermissions", user.getPermissions())
			.assign("saved", req.getParameter("saved") != null ? "true" : "false")
			.send(resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Enumeration<String> paramNames = req.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			LogManager.getLogger().debug("Param Name: " + paramName);
			LogManager.getLogger().debug("Param value: " + req.getParameter(paramName));
		}
		
		Context ctx = Context.get(req);
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		if(!auth.getUser().hasPermission("nightweb.admin.canEditPermissions")) {
			ctx.getTemplate("admin/permissionError.tpl").send(resp);
			return;
		}
		
		String sUserId = new PathParameters(req.getPathInfo()).getParameter(0);
		
		long userId = 0L;
		
		LongValidator val = LongValidator.getInstance();
		if(val.isValid(sUserId)) {
			userId = Long.parseLong(sUserId);
		}
		
		if(userId == 0L) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownUserError")
				.send(resp);
			return;
		}
		
		UserService userv = ctx.getServiceManager().getService(UserService.class);
		User user = userv.getOne(userId);
		
		if(user == null) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "unknownUserError")
				.send(resp);
			return;
		}
		
		if(!auth.getUser().canEdit(user) && auth.getUser().getId() != user.getId()) {
			ctx.getTemplate("admin/userEdit.tpl")
				.assign("currentUser", auth.getUser())
				.assign("failed", "insufficientPermissionError")
				.send(resp);
			return;
		}
		
		PermissionService permServ = ctx.getServiceManager().getService(PermissionService.class);
		List<DefaultPermission> permissions = permServ.getAll();
		
		for(DefaultPermission permission : permissions) {
			UserPermission userPerm = new UserPermission(user, permission);
			
			String param = req.getParameter("permission-" + permission.getName());
			
			if(param != null) {
				switch(permission.getType()) {
					case STRING:
					case NUMBER:
						userPerm.setValue(param);
						break;
					case FLAG:
						if(param.equalsIgnoreCase("ALLOW")) {
							userPerm.setValue(Permission.Tribool.TRUE.getAsString());
						} else if(param.equalsIgnoreCase("DENY")) {
							userPerm.setValue(Permission.Tribool.FALSE.getAsString());
						} else {
							userPerm.setValue(Permission.Tribool.NEUTRAL.getAsString());
						}
						break;
				}
				
				if(userPerm.getValue().equalsIgnoreCase(Tribool.NEUTRAL.getAsString()) || userPerm.getValue().equalsIgnoreCase(Tribool.FALSE.getAsString())) {
					LogManager.getLogger().debug("Permission neutral or false: " + permission.getName());
					user.removePermission(permission.getName());
				} else {
					user.addPermission(userPerm);
				}
			} else {
				if(user.getPermission(permission.getName()) != null) {
					user.removePermission(permission.getName());
				}
			}
		}
		
		userv.save(user);
		resp.sendRedirect(req.getRequestURI() + "?saved");
	}

}
