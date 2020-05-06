package dev.teamnight.nightweb.core.servlets;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dev.teamnight.nightweb.core.Authenticated;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.entities.Group;
import dev.teamnight.nightweb.core.entities.User;
import dev.teamnight.nightweb.core.entities.UserPermission;
import dev.teamnight.nightweb.core.service.UserService;

@Authenticated
public class TestServlet extends HttpServlet {
	
	@Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException
    {
		Context ctx = Context.get(request);
		UserService us = ctx.getServiceManager().getService(UserService.class);
		
		User user;
		
		if((user = us.getByUsername("test")) != null) {
		} else {
			user = new User("test", "email");
			user.setSalt("salt");
			user.setPassword(user.createHash("test"));
			user.setRegistrationDate(new Date());
			user.setLastLoginDate(null);
			user.setDisabled(false);
			user.setGroups(new ArrayList<Group>());
			user.setPermissions(new ArrayList<UserPermission>());
			
			us.save(user);
		}
		
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        response.getWriter().println("<h1>Hello from HelloServlet</h1><br><p>Hello <b>" + user.getUsername() + "</b> with id " + user.getId() + "</p>");
    }
}
