/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.mvc.annotations.GET;
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
	@Produces("application/json")
	@Path("/index/:username")
	public Result indexAction(@PathParam("username") String username, HttpServletRequest req) {
		List<String> test = new ArrayList<String>();
		test.add(username);
		return ok(test);
	}
}
