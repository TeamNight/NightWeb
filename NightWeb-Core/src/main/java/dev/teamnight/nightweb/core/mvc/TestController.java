/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.annotations.Authenticated;
import dev.teamnight.nightweb.core.mvc.annotations.Accepts;
import dev.teamnight.nightweb.core.mvc.annotations.Authorized;
import dev.teamnight.nightweb.core.mvc.annotations.GET;
import dev.teamnight.nightweb.core.mvc.annotations.Method;
import dev.teamnight.nightweb.core.mvc.annotations.POST;
import dev.teamnight.nightweb.core.mvc.annotations.Path;
import dev.teamnight.nightweb.core.mvc.annotations.PathParam;
import dev.teamnight.nightweb.core.mvc.annotations.Produces;

/**
 * @author Jonas
 *
 */
public class TestController extends Controller {

	/**
	 * @param ctx
	 */
	public TestController(Context ctx) {
		super(ctx);
	}

	@GET
	@Authenticated
	@Produces("application/json")
	@Path("/index/:username")
	public Result indexAction(@PathParam("username") String username, HttpServletRequest req) {
		List<String> test = new ArrayList<String>();
		test.add(username);
		
		Logger log = LogManager.getLogger();
		Enumeration<String> enumerate = req.getAttributeNames();
		while(enumerate.hasMoreElements()){
			log.debug("AttributeName: " + enumerate.nextElement());
		}
		
		return ok(test);
	}
	
	@GET
	@Produces("application/xml")
	@Path("/index/:username")
	public Result indexXMLAction(@PathParam("username") String username) {
		return ok("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><username>" + username + "</username>");
	}
	
	@GET
	@Authorized("nightweb.moderator.canSuspendUsers")
	@Path("/index/:username")
	public Result indexAction(@PathParam("username") String username) {
		return ok("Hello, " + username);
	}
	
	@POST
	@Path("/index/:username")
	public Result indexPostAction(@PathParam("username") String username) {
		return ok("We already know you, " + username);
	}
	
	@Method("DELETE")
	@Path("/index/:username")
	public Result indexDeleteAction(@PathParam("username") String username) {
		return ok("We will forget you, " + username);
	}
	
	@Method("PUT")
	@Produces("application/xml")
	@Path("/testAction/:username")
	public Result indexPutAction(@PathParam("username") String username) {
		return ok("Test successful:2");
	}
	
	@POST
	@Accepts("application/json")
	@Path("/testAction")
	public Result testAction() {
		return ok("Test successful");
	}
}
