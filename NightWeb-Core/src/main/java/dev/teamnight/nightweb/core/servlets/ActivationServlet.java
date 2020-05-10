/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.EmailValidator;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.PathParameters;
import dev.teamnight.nightweb.core.WebSession;
import dev.teamnight.nightweb.core.entities.ActivationType;
import dev.teamnight.nightweb.core.entities.Setting;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.service.SettingService;
import dev.teamnight.nightweb.core.service.UserService;

/**
 * @author Jonas
 *
 */
public class ActivationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		WebSession session = WebSession.getSession(req);
		
		if(session.isLoggedIn()) {
			resp.sendRedirect("/");
		}
		
		Context ctx = Context.get(req);
		
		//Check if activationType is ACTIVATION
		SettingService setServ = ctx.getServiceManager().getService(SettingService.class);
		Setting activationType = setServ.getByKey("activationType");
		
		if(activationType.getAsEnum(ActivationType.class) == ActivationType.ACTIVATION) {
			PathParameters params = new PathParameters(req.getPathInfo());
			
			if(params.size() != 2) {
				ctx.getTemplate("activation.tpl").assign("failed", "missingParams").send(resp);
				return;
			}
			
			String email = params.getParameter(0);
			String code = params.getParameter(1);
			
			//Check parameters
			EmailValidator emailVal = EmailValidator.getInstance();
			if(!emailVal.isValid(email)) {
				ctx.getTemplate("activation.tpl").assign("failed", "invalidEmailError").send(resp);
				return;
			}
			
			if(!code.matches("^[a-zA-Z0-9]{16}$")) {
				ctx.getTemplate("activation.tpl").assign("failed", "invalidCodeError").send(resp);
				return;
			}
			
			UserService userServ = ctx.getServiceManager().getService(UserService.class);
			
			User user = userServ.getByEmail(email);
			
			//Check if no user is found by this email
			if(user == null) {
				ctx.getTemplate("activation.tpl").assign("failed", "unknownUserError").send(resp);
				return;
			}
			
			if(!user.isDisabled() || user.getActivationKey() == null) {
				ctx.getTemplate("activation.tpl").assign("failed", "alreadyActivatedError").send(resp);
				return;
			}
			
			if(user.getActivationKey().equals(code)) {
				user.setActivationKey(null);
				user.setDisabled(false);
				userServ.save(user);
				ctx.getTemplate("activation.tpl").assign("user", user).send(resp);
			} else {
				ctx.getTemplate("activation.tpl").assign("failed", "invalidCodeError").send(resp);
			}
		} else {
			resp.sendRedirect("/");
		}
	}
	
}
