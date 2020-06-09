/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import dev.teamnight.nightweb.core.mvc.RequestParameter;
import dev.teamnight.nightweb.core.mvc.Result;
import dev.teamnight.nightweb.core.mvc.RouteQuery;
import dev.teamnight.nightweb.core.mvc.Router;
import dev.teamnight.nightweb.core.mvc.ServletRouterImpl;
import dev.teamnight.nightweb.core.util.StringUtil;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.MethodHolder;
import dev.teamnight.nightweb.core.mvc.PathResolver;

/**
 * @author Jonas
 *
 */
public class RouterTest {

	private PathResolver resolver = new PathResolver();
	private Context ctx = new TestApplicationContext();
	
	@Test
	public void testPathResolver() {
		Pattern testPattern = resolver.compilePathSpec("/admin/user/:username/edit");
		
		Matcher matcher = testPattern.matcher("/admin/user/nightloewe/edit");
		
		assertTrue("Matcher does not match right url", matcher.matches());
		
		Matcher matcher2 = testPattern.matcher("/admin/user/edit");
		
		assertFalse("Matcher2 matches url", matcher2.matches());
		
		Map<RequestParameter, String> params = resolver.resolvePathParameters(testPattern, new RequestParameter[] {new RequestParameter(true, "username")}, "/admin/user/nightloewe/edit");
		
		assertTrue("Params size has to be 1", params.size() == 1);
		assertTrue("Parameter values not nightloewe", params.get(new RequestParameter(true, "username")).equalsIgnoreCase("nightloewe"));
	}
	
	@Test
	public void addRouteTest() {
		Router router = new ServletRouterImpl(null);
		
		Controller c = new TestController(null);
		
		try {
			router.addRoute(c.getClass().getMethod("testAction", String.class), c);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		for(MethodHolder h : router.getRoutes()) {
			System.out.println("MethodHolder --\n"
					+ "PathSpec: " + h.getPathSpec()
					+ "\nPattern: " + h.getRegex().pattern()
					+ "\nHTTP-Method: " + h.getHttpMethod()
					+ "\nProduces" + h.getProduces()
					+ "\nAccepts: " + h.getAccepts()
					+ "\nParemeters: " + String.join(", ", h.getParameters().entrySet().stream().map(entry -> entry.getKey().getName() + "-" + entry.getValue()).toArray(String[]::new)));
		}
	}
	
	@Test
	public void invokeTest() {
		Router router = new ServletRouterImpl(null);
		
		Controller c = new TestController(null);
		
		try {
			router.addRoute(c.getClass().getMethod("testAction", String.class), c);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new AssertionError(e);
		}
		
		MethodHolder holder = router.getRoutes().get(0);
		
		Map<RequestParameter, String> map = resolver.resolvePathParameters(holder.getRegex(), 
				holder.getParameters().keySet().toArray(new RequestParameter[holder.getParameters().size()]), 
				"/api/test/testString");
		
		try {
			Result res = holder.executeMethod(null, null, map);
			
			System.out.println("Result: " + res.status() + " " + res.content());
			
			assertTrue("Result not okay", res.content().equals("test var is: testString"));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}
	
	@Test
	public void otherTests() throws NoSuchMethodException, SecurityException {
		Router router = new ServletRouterImpl(this.ctx);
		
		Controller c = new TestController(null);
		
		try {
			router.addRoute(c.getClass().getMethod("testAction", String.class), c);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new AssertionError(e);
		}	
		
		assertTrue("Context path not /", router.getContextPath().equals("/"));
		assertTrue("Controller path not /", router.getPath(c).equals("/"));
		assertTrue("Method Path not /api/test/:test", router.getPath(c, c.getClass().getMethod("testAction", String.class)).equals("/api/test/:test"));
		assertTrue("ControllerAndMethodName Path not /api/test/:test", router.getPath("dev.teamnight.TestController.testAction").equals("/api/test/:test"));
		assertTrue("MetholdHolder null", router.getMethod(RouteQuery.of("/api/test/:test")) != null);
		assertTrue("MetholdHolder null", router.getMethodByURL(RouteQuery.of("/api/test/testString")) != null);
	}
	
	@Test
	public void acceptHeaderParsingTest() {
		List<String> acceptHeaders = StringUtil.parseAcceptHeader("text/html,application/html+xml;q=0.5");
		
		assertTrue("acceptHeaders size not 2", acceptHeaders.size() == 2);
		assertTrue("text/html not first", acceptHeaders.get(0).equalsIgnoreCase("text/html"));
		assertTrue("application/html+xml not second", acceptHeaders.get(1).equalsIgnoreCase("application/html+xml"));
	}
}
