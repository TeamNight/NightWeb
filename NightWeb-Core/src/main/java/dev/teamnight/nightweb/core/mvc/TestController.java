/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import javax.servlet.http.HttpServletRequest;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.mvc.annotations.GET;
import dev.teamnight.nightweb.core.mvc.annotations.Path;
import dev.teamnight.nightweb.core.mvc.annotations.PathParam;

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
	@Path("/index/:username?")
	public Result indexAction(@PathParam(value = "string", required = false) String string, HttpServletRequest req) {
		return ok("Hello, World");
	}
}
