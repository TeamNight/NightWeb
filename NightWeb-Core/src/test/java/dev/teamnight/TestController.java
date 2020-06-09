/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.Result;
import dev.teamnight.nightweb.core.mvc.annotations.Accepts;
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
	@Produces("text/html")
	@Accepts("*")
	@Path("/api/test/:test")
	public Result testAction(@PathParam("test") String test) {
		return ok("test var is: " + test);
	}

}
