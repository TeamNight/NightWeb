/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.NightWeb;
import dev.teamnight.nightweb.core.annotations.Authenticated;
import dev.teamnight.nightweb.core.events.RouteAddedEvent;
import dev.teamnight.nightweb.core.mvc.Controller;
import dev.teamnight.nightweb.core.mvc.FilterEntry;
import dev.teamnight.nightweb.core.mvc.InvokedRoute;
import dev.teamnight.nightweb.core.mvc.PathResolver;
import dev.teamnight.nightweb.core.mvc.RequestParameter;
import dev.teamnight.nightweb.core.mvc.Result;
import dev.teamnight.nightweb.core.mvc.Route;
import dev.teamnight.nightweb.core.mvc.RouteQuery;
import dev.teamnight.nightweb.core.mvc.Router;
import dev.teamnight.nightweb.core.mvc.SecurityFilter;
import dev.teamnight.nightweb.core.mvc.annotations.Accepts;
import dev.teamnight.nightweb.core.mvc.annotations.Authorized;
import dev.teamnight.nightweb.core.mvc.annotations.GET;
import dev.teamnight.nightweb.core.mvc.annotations.POST;
import dev.teamnight.nightweb.core.mvc.annotations.Path;
import dev.teamnight.nightweb.core.mvc.annotations.PathParam;
import dev.teamnight.nightweb.core.mvc.annotations.Produces;
import dev.teamnight.nightweb.core.mvc.annotations.QueryParam;
import dev.teamnight.nightweb.core.util.StringUtil;

/**
 * The Router class
 * @author Jonas
 *
 */
public class ServletRouterImpl extends GenericServlet implements Router {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LogManager.getLogger(ServletRouterImpl.class);
	
	private Context ctx;
	private PathResolver pathResolver;
	
	private List<Route> routes = new ArrayList<Route>();
	private List<Controller> controllers = new ArrayList<Controller>();

	private SecurityFilter authFilter;
	private SecurityFilter adminFilter;

	public ServletRouterImpl(Context ctx, SecurityFilter authFilter, SecurityFilter adminFilter) {
		this.ctx = ctx;
		this.pathResolver = new PathResolver();
		this.authFilter = authFilter;
		this.adminFilter = adminFilter;
	}
	
	// ----------------------------------------------------------------------- //
	// GenericServlet                                                          //
	// ----------------------------------------------------------------------- //
	
	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		if(!(req instanceof HttpServletRequest && res instanceof HttpServletResponse)) {
			throw new ServletException("non-HTTP request or response");
		}
		
		this.service((HttpServletRequest) req, (HttpServletResponse) res);
	}
	
	// ----------------------------------------------------------------------- //
	// Handling                                                                //
	// ----------------------------------------------------------------------- //
	
	/**
	 * Main method for the Router to be called when a route is accessed
	 * @throws IOException 
	 * @throws ServletException 
	 */
	protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		String url = req.getPathInfo();
		List<String> acceptHeaders = StringUtil.parseAcceptHeader(req.getHeader("Accept") == null ? "" : req.getHeader("Accept"));
		
		Route route = null;
		RouteQuery query = RouteQuery.of(url).method(req.getMethod()).accepts(req.getContentType());
		
		if(!acceptHeaders.isEmpty()) {
			for(String acceptHeader : acceptHeaders) {
				query.produces(acceptHeader);
				
				route = this.getRouteByURL(query);
				
				if(route != null) {
					break;
				}
			}
		} else {
			route = this.getRouteByURL(query);
		}
		
		if(route == null) {
			String failureReason = this.getFailureReason(query);
			
			switch(failureReason) {
				case "URL_NOT_MATCHING":
					res.sendError(HttpStatus.NOT_FOUND_404, "The request url <b>" + req.getRequestURI() + "</b> was not found.");
					return;
				case "METHOD_NOT_MATCHING":
					res.sendError(HttpStatus.METHOD_NOT_ALLOWED_405, "The method " + query.method() + " is not allowed for the path <b>" + req.getRequestURI() + "</b>.");
					return;
				case "PRODUCES_NOT_MATCHING":
					res.sendError(HttpStatus.BAD_REQUEST_400, "The Accept-Headers are not allowed for <b>" + req.getRequestURI() + "</b>.");
					return;
				case "ACCEPTS_NOT_MATCHING":
					res.sendError(HttpStatus.BAD_REQUEST_400, "Request Content-Type is not allowed");
					return;
				default:
					res.sendError(HttpStatus.INTERNAL_SERVER_ERROR_500, "Unknown error occured while searching routes");
					return;
			}
		}
		
		Map<RequestParameter, String> params = this.pathResolver.resolvePathParameters(
				route.getCompiledPathSpec(), 
				route.getParameters().keySet().toArray(new RequestParameter[route.getParameters().size()]), 
				url);
		
		try {
			Result result = route.execute(req, res, params);
			
			if(result.redirect().isPresent()) {
				res.sendRedirect(result.redirect().get());
				return;
			}
			
			if(result.status() > 399) {
				if(result.statusMessage().isPresent()) {
					res.sendError(result.status(), result.statusMessage().get());
				} else {
					res.sendError(result.status());
				}
				
				return;
			}
			
			if(result.contentType().equalsIgnoreCase("application/json")) {
				result.content(NightWeb.getGson().toJson(result.data()));
			}
			
			res.setStatus(result.status());
			res.setContentType(result.contentType());
			res.setCharacterEncoding(result.characterEncoding());
			res.getWriter().write(result.content());
			
			result.cookies().forEach(cookie -> res.addCookie(cookie));
			result.headers().forEach((name, value) -> res.addHeader(name, value));
		} catch (ServletException e) {
			if(e.getCause() instanceof IllegalAccessException) {
				InvokedRoute invokedRoute = (InvokedRoute) route;
				
				LOGGER.error("Unable to access method " + route.getController().getClass().getCanonicalName() + "." + invokedRoute.getUnderlyingMethodName() + " - maybe it is private or not exported?");
			} else {
				throw e;
			}
		}
	}
	
	// ----------------------------------------------------------------------- //
	// Router implementation                                                   //
	// ----------------------------------------------------------------------- //
	
	@Override
	public void addController(Controller controller) {
		Class<?> clazz = controller.getClass();
		
		if(this.controllers.stream().map(c -> c.getClass()).filter(cl -> cl == clazz).findAny().orElse(null) != null) {
			return;
		}
		
		this.controllers.add(controller);
		
		for(Method method : clazz.getMethods()) {
			this.addRoute(method, controller);
		}
	}
	
	@Override
	public void addRoute(Method method, Controller controller) {
		MethodHolder holder = new MethodHolder(this.ctx, method, controller);
		
		Path pathAnnotation = method.getAnnotation(Path.class);
		if(pathAnnotation == null) {
			LOGGER.debug("path annotation not present on " 
					+ method.getName() 
					+ "(" + String.join(",", Arrays.stream(method.getParameters()).map(param -> param.getType().getName() + " " + param.getName()).toArray(String[]::new)));
			return;
		}
		
		Path controllerPath = controller.getClass().getAnnotation(Path.class);
		
		holder.setPathSpec(StringUtil.filterURL((controllerPath == null ? "" : controllerPath.value()) + pathAnnotation.value()));
		holder.setCompiledPathSpec(this.pathResolver.compilePathSpec(pathAnnotation.value()));
		
		dev.teamnight.nightweb.core.mvc.annotations.Method methodAnnotation = method.getAnnotation(dev.teamnight.nightweb.core.mvc.annotations.Method.class);
		if(methodAnnotation != null) {
			holder.setHttpMethod(methodAnnotation.value());
		}
		
		if(holder.getHttpMethod() == null) {
			GET getAnnotation = method.getAnnotation(GET.class);
			
			if(getAnnotation != null) {
				holder.setHttpMethod("GET");
			} else {
				POST postAnnoation = method.getAnnotation(POST.class);
				
				if(postAnnoation != null) {
					holder.setHttpMethod("POST");
				} else {
					throw new IllegalArgumentException("no GET, POST or Method annotation present on " + method.toGenericString());
				}
			}
		}
		
		Accepts acceptsAnnotation = method.getAnnotation(Accepts.class);
		if(acceptsAnnotation != null) {
			holder.setAccepts(acceptsAnnotation.value());
		}
		
		Produces producesAnnotation = method.getAnnotation(Produces.class);
		if(producesAnnotation != null) {
			holder.setProduces(producesAnnotation.value());
		}
		
		Map<RequestParameter, Integer> parametersMap = new HashMap<RequestParameter, Integer>();
		
		int i = 0;
		for(Parameter param : method.getParameters()) {
			if(param.getType() == HttpServletRequest.class) {
				holder.setPosReq(i++);
				continue;
			} else if(param.getType() == HttpServletResponse.class) {
				holder.setPosRes(i++);
				continue;
			} else if(Context.class.isAssignableFrom(param.getType())) {
				holder.setPosCtx(i++);
				continue;
			} else if(param.getType() == Map.class) {
				holder.setPosParamMap(i++);
				continue;
			}
			
			PathParam pathParam = param.getAnnotation(PathParam.class);
			if(pathParam != null) {
				parametersMap.put(new RequestParameter(true, pathParam.value()), i++);
				continue;
			}
			
			QueryParam queryParam = param.getAnnotation(QueryParam.class);
			if(queryParam != null) {
				parametersMap.put(new RequestParameter(false, queryParam.value()), i++);
			}
			
			throw new IllegalArgumentException("Method has unallowed parameter " + param.getName());
		}
		
		holder.setParameters(parametersMap);
		
		this.addRoute(holder);
	}
	
	@Override
	public void addRoute(Route route) {
		FilterEntry entry = new FilterEntry(
				this.pathResolver.compilePathSpec(StringUtil.filterURL(this.getContextPath() + route.getPathSpec())).pattern(), 
				route.getHttpMethod()
				);
		entry.addProduces(route.getProduces());
		
		if(route.getAccepts().isPresent()) {
			entry.addAccepts(route.getAccepts().get());
		}
		
		Class<?> routeClass = route.getClass();
		
		//Authenticated annotation
		Authenticated authAnnotation = routeClass.getAnnotation(Authenticated.class);
		if(authAnnotation != null) {
			this.authFilter.addPattern(entry);
		}
		
		//Authorization annotation
		Authorized authorizedAnnotation = routeClass.getAnnotation(Authorized.class);
		if(authorizedAnnotation != null) {
			entry.addAttribute("dev.teamnight.nightweb.core.permission", authorizedAnnotation.value());
			
			this.adminFilter.addPattern(entry);
		}
		
		LOGGER.info("Registering route »" + route.getPathSpec() + "« using Method " + route.getHttpMethod() + "");
		
		this.routes.add(route);
		
		NightWeb.getEventManager().fireEvent(new RouteAddedEvent(this.ctx, route));
	}
	
	@Override
	public List<Route> getRoutes() {
		return Collections.unmodifiableList(this.routes);
	}
	
	@Override
	public String getContextPath() {
		return this.ctx.getContextPath();
	}
	
	@Override
	public PathResolver getPathResolver() {
		return this.pathResolver;
	}
	
	@Override
	public String getPath(Controller controller) {
		Path pathAnnotation = controller.getClass().getAnnotation(Path.class);
		
		if(pathAnnotation == null) {
			return this.ctx.getContextPath();
		}
		
		return StringUtil.filterURL(this.ctx.getContextPath() + pathAnnotation.value());
	}
	
	@Override
	public String getPath(Controller controller, Method method) {
		Route route = this.routes.stream().filter(h -> h.getHttpMethod().equals(method)).findAny().orElse(null);
		
		if(route == null) {
			return null;
		}
		
		return StringUtil.filterURL(this.ctx.getContextPath() + route.getPathSpec());
	}
	
	@Override
	public String getPath(String controllerAndMethodName) {
		List<String> contents = Arrays.stream(controllerAndMethodName.split("\\.")).collect(Collectors.toList());
		
		String methodName = contents.get(contents.size() - 1);
		contents.remove(contents.size() - 1);
		
		String controllerName = String.join(".", contents);
		
		for(Route route : this.routes) {
			if(route instanceof InvokedRoute) {
				InvokedRoute invokedRoute = (InvokedRoute) route;
				
				if(invokedRoute.getUnderlyingClassName().equalsIgnoreCase(controllerName)) {
					if(invokedRoute.getUnderlyingMethodName().equalsIgnoreCase(methodName)) {
						return StringUtil.filterURL(this.ctx.getContextPath() + route.getPathSpec());
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public Controller getControllerByURL(String url) {
		Route holder = this.routes.stream().filter(h -> h.getCompiledPathSpec().matcher(url).matches()).findAny().orElse(null);
		
		if(holder.getController().isEmpty()) {
			for(Controller controller : this.controllers) {
				Path path = controller.getClass().getAnnotation(Path.class);
				if(path != null) {
					Pattern p = this.pathResolver.compilePathSpec(path.value());
					if(p.matcher(url).matches()) {
						return controller;
					}
				}
			}
		}
		
		return null;
	}
	
	public String getFailureReason(RouteQuery query) {
		List<Route> holders = this.routes.stream().filter(h -> h.getCompiledPathSpec().matcher(query.url()).matches()).collect(Collectors.toList());
		
		if(holders.size() == 0) {
			return "URL_NOT_MATCHING";
		}
		
		holders = holders.stream().filter(h -> h.getHttpMethod().equalsIgnoreCase(query.method())).collect(Collectors.toList());
		
		if(holders.size() == 0) {
			return "METHOD_NOT_MATCHING";
		}
		
		final String produces = query.produces().isPresent() ? query.produces().get() : "text/html";
		
		holders = holders.stream().filter(h -> h.getProduces().equalsIgnoreCase(produces)).collect(Collectors.toList());
		
		if(holders.size() == 0) {
			return "PRODUCES_NOT_MATCHING";
		}
		
		holders = holders.stream().filter(h -> h.getAccepts().isPresent()).collect(Collectors.toList());
		
		if(holders.size() > 0) {
			if(query.accepts().isPresent()) {
				holders = holders.stream().filter(h -> query.accepts().get().equalsIgnoreCase(h.getAccepts().get())).collect(Collectors.toList());
				
				if(holders.size() == 0) {
					return "ACCEPTS_NOT_MATCHING";
				} else {
					return null;
				}
			}
			return "ACCEPTS_NOT_MATCHING";
		}

		return null;
	}
	
	@Override
	public Route getRouteByURL(RouteQuery query) {
		return this.routes.stream()
				.filter(h -> h.getCompiledPathSpec().matcher(query.url()).matches())
				.filter(h -> h.getHttpMethod().equalsIgnoreCase(query.method()))
				.filter(h -> {
					if(query.produces().isPresent()) {
						return h.getProduces().equalsIgnoreCase(query.produces().get());
					} else {
						return h.getProduces().equalsIgnoreCase("text/html");
					}
				})
				.filter(h -> {
					if(h.getAccepts().isPresent()) {
						if(query.accepts().isPresent()) {
							return query.accepts().get().equalsIgnoreCase(h.getAccepts().get());
						} else {
							return false;
						}
					} else {
						return true;
					}
				})
				.findFirst()
				.orElse(null);
	}
	
	@Override
	public Controller getController(String pathSpec) {
		return this.controllers.stream()
				.filter(c -> this.getPath(c).substring(this.getContextPath().length()).equals(pathSpec))
				.findAny()
				.orElse(null);
	}
	
	@Override
	public Route getRoute(RouteQuery query) {
		return this.routes.stream()
				.filter(h -> h.getPathSpec().equals(query.url()))
				.filter(h -> h.getHttpMethod().equalsIgnoreCase(query.method()))
				.filter(h -> {
					if(query.produces().isPresent()) {
						return h.getProduces().equalsIgnoreCase(query.produces().get());
					} else {
						return h.getProduces().equalsIgnoreCase("text/html");
					}
				})
				.filter(h -> {
					if(h.getAccepts().isPresent()) {
						if(query.accepts().isPresent()) {
							return query.accepts().get().equalsIgnoreCase(h.getAccepts().get());
						} else {
							return false;
						}
					} else {
						return true;
					}
				})
				.findFirst()
				.orElse(null);
	}
	
	public boolean pathExists(String path) {
		return this.routes.stream()
				.filter(h -> h.getCompiledPathSpec().matcher(path).matches())
				.findAny()
				.orElse(null) 
				!= null;
	}

}
