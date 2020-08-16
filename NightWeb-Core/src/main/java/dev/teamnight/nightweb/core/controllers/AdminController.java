/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.util.URIUtil;

import dev.teamnight.nightweb.core.Authenticator;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.annotations.Authenticated;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.Result;
import dev.teamnight.nightweb.core.mvc.annotations.Authorized;
import dev.teamnight.nightweb.core.mvc.annotations.GET;
import dev.teamnight.nightweb.core.mvc.annotations.POST;
import dev.teamnight.nightweb.core.mvc.annotations.Path;
import dev.teamnight.nightweb.core.service.UserService;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * @author Jonas
 *
 */
public class AdminController extends Controller {

	public AdminController(Context ctx) {
		super(ctx);
	}
	
	@GET
	@Authenticated
	@Authorized
	@Path("/admin")
	public Result dashboardAction(Context ctx, HttpServletRequest req) {
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		
		//Build the template
		return ok().content(ctx.getTemplate("admin/dashboard.tpl")
				.assign("currentUser", auth.getUser())
				.assign("implementationName", NightWeb.getCoreApplication().getImplementationName())
				.assign("version", NightWeb.getCoreApplication().getVersion())
				.build());
	}
	
	@GET
	@Path("/admin/login")
	public Result loginAction(Context ctx, HttpServletRequest req) {
		if(req.getSession().getAttribute("dev.teamnight.nightweb.core.session.adminSession") != null) {
			return ok().redirect(ctx.getContextPath() + "/admin");
		}
		
		return ok().content(ctx.getTemplate("admin/login.tpl").build());
	}
	
	@POST
	@Path("/admin/login")
	public Result loginProcessAction(Context ctx, HttpServletRequest req) {
		if(req.getSession().getAttribute("dev.teamnight.nightweb.core.session.adminSession") != null) {
			return ok().redirect(ctx.getContextPath() + "/admin");
		}
		
		//Check parameters
		String emailOrUsername = req.getParameter("emailOrUsername");
		String password = req.getParameter("password");
		
		if(emailOrUsername == null || password == null) {
			return ok().content(ctx.getTemplate("admin/login.tpl").assign("failed", "userOrPasswordNullError").build());
		}
		
		EmailValidator emailVal = EmailValidator.getInstance();
		
		boolean email = false;
		
		if(!emailVal.isValid(emailOrUsername)) {
			if(!emailOrUsername.matches("[A-Za-zäöüÄÖÜß_-]{1,50}")) {
				return ok().content(ctx.getTemplate("admin/login.tpl").assign("failed", "unknownUserError").build());
			}
		} else {
			email = true;
		}
		
		UserService userServ = ctx.getServiceManager().getService(UserService.class);
		
		User user;
		if(email) {
			user = userServ.getByEmail(emailOrUsername);
		} else {
			user = userServ.getByUsername(emailOrUsername);
		}
		
		//Check if user has permission to enter ACP
		if(!user.hasPermission("nightweb.admin.canUseACP")) {
			return ok().content(ctx.getTemplate("admin/login.tpl").assign("failed", "notAdminError").build());
		}
		
		//Check user password
		Authenticator auth = ctx.getAuthenticator(req.getSession());
		auth.setUser(user);
		
		if(!auth.authenticate(password)) {
			return ok().content(ctx.getTemplate("admin/login.tpl").assign("failed", "invalidPasswordError").build());
		}
		
		req.getSession().setAttribute("dev.teamnight.nightweb.core.session.adminSession", true);
		
		LogManager.getLogger().debug("CTXPATH: " + ctx.getContextPath());
		LogManager.getLogger().debug("CTXPATH ENC: " + URIUtil.encodePath(ctx.getContextPath()));
		
		return ok().redirect(StringUtil.filterURL(ctx.getContextPath() + "/admin"));
	}

}
