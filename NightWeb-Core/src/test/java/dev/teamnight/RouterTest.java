/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.junit.Test;

import dev.teamnight.nightweb.core.mvc.RequestParameter;
import dev.teamnight.nightweb.core.mvc.Result;
import dev.teamnight.nightweb.core.mvc.Route;
import dev.teamnight.nightweb.core.mvc.RouteQuery;
import dev.teamnight.nightweb.core.mvc.Router;
import dev.teamnight.nightweb.core.util.StringUtil;
import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.impl.ApplicationContextImpl;
import dev.teamnight.nightweb.core.impl.MethodHolder;
import dev.teamnight.nightweb.core.impl.ServletRouterImpl;
import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.PathResolver;

/**
 * @author Jonas
 *
 */
public class RouterTest {

	private PathResolver resolver = new PathResolver();
	private Context ctx = new ApplicationContextImpl(null, null, null, null);
	
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
		Router router = new ServletRouterImpl(null, null, null);
		
		Controller c = new TestController(null);
		
		try {
			router.addRoute(c.getClass().getMethod("testAction", String.class), c);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		for(Route h : router.getRoutes()) {
			System.out.println("MethodHolder --\n"
					+ "PathSpec: " + h.getPathSpec()
					+ "\nPattern: " + h.getCompiledPathSpec().pattern()
					+ "\nHTTP-Method: " + h.getHttpMethod()
					+ "\nProduces" + h.getProduces()
					+ "\nAccepts: " + h.getAccepts()
					+ "\nParemeters: " + String.join(", ", h.getParameters().entrySet().stream().map(entry -> entry.getKey().getName() + "-" + entry.getValue()).toArray(String[]::new)));
		}
	}
	
	@Test
	public void invokeTest() {
		Router router = new ServletRouterImpl(null, null, null);
		
		Controller c = new TestController(null);
		
		try {
			router.addRoute(c.getClass().getMethod("testAction", String.class), c);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new AssertionError(e);
		}
		
		Route holder = router.getRoutes().get(0);
		
		Map<RequestParameter, String> map = resolver.resolvePathParameters(holder.getCompiledPathSpec(), 
				holder.getParameters().keySet().toArray(new RequestParameter[holder.getParameters().size()]), 
				"/api/test/testString");
		
		try {
			Result res = holder.execute(null, null, map);
			
			System.out.println("Result: " + res.status() + " " + res.content());
			
			assertTrue("Result not okay", res.content().equals("test var is: testString"));
		} catch (IllegalArgumentException | ServletException e) {
			throw new AssertionError(e);
		}
	}
	
	@Test
	public void otherTests() throws NoSuchMethodException, SecurityException {
		Router router = new ServletRouterImpl(this.ctx, null, null);
		
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
		assertTrue("MetholdHolder null", router.getRoute(RouteQuery.of("/api/test/:test")) != null);
		assertTrue("MetholdHolder null", router.getRouteByURL(RouteQuery.of("/api/test/testString")) != null);
	}
	
	@Test
	public void acceptHeaderParsingTest() {
		List<String> acceptHeaders = StringUtil.parseAcceptHeader("text/html,application/html+xml;q=0.5");
		
		assertTrue("acceptHeaders size not 2", acceptHeaders.size() == 2);
		assertTrue("text/html not first", acceptHeaders.get(0).equalsIgnoreCase("text/html"));
		assertTrue("application/html+xml not second", acceptHeaders.get(1).equalsIgnoreCase("application/html+xml"));
	}
	
	@Test
	public void routingTest() {
		HttpServletRequest req = this.createMockRequest("/api/test/testString", "GET", "text/html,application/json;q=0.9");
		HttpServletResponse res = new MockResponse();
		
		Router router = new ServletRouterImpl(this.ctx, null, null);
		
		Controller c = new TestController(null);
		
		try {
			router.addRoute(c.getClass().getMethod("testAction", String.class), c);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new AssertionError(e);
		}
		
		ServletRouterImpl servlet = (ServletRouterImpl) router;
		
		try {
			servlet.service(req, res);
		} catch (ServletException | IOException | NullPointerException e) {
			e.printStackTrace();
			throw new AssertionError(e);
		}
		
		System.out.println("MockResponse --\nStatus: " + res.getStatus());
		System.out.println("Message: " + ((MockResponse)res).getErrorMsg());
		System.out.println("Content-Type: " + res.getContentType());
		System.out.println("Character Encoding: "+ res.getCharacterEncoding());
	}
	
	public HttpServletRequest createMockRequest(String url, String httpMethod, String acceptHeader) {
		MockRequest req = new MockRequest(url, httpMethod, "");
		req.addHeader("User-Agent", "Mock-Client");
		req.addHeader("Host", "localhost");
		req.addHeader("Accept-Language", "en-us");
		req.addHeader("Accept", "text/html,application/json;q=0.9");
		req.addHeader("Accept-Encoding", "gzip, deflate, utf-8");
		req.addHeader("Connection", "Keep-Alive");
		
		return req;
	}
}
