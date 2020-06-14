/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import java.util.stream.Stream;

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
import dev.teamnight.nightweb.core.mvc.annotations.Accepts;
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
	
	private List<MethodHolder> routes = new ArrayList<MethodHolder>();
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
		
		MethodHolder holder = null;
		RouteQuery query = RouteQuery.of(url).method(req.getMethod()).accepts(req.getContentType());
		
		if(!acceptHeaders.isEmpty()) {
			for(String acceptHeader : acceptHeaders) {
				query.produces(acceptHeader);
				
				holder = this.getMethodByURL(query);
				
				if(holder != null) {
					break;
				}
			}
		} else {
			holder = this.getMethodByURL(query);
		}
		
		if(holder == null) {
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
				holder.getRegex(), 
				holder.getParameters().keySet().toArray(new RequestParameter[holder.getParameters().size()]), 
				url);
		
		try {
			Result result = holder.executeMethod(req, res, params);
			
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
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ServletException(e);
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
		holder.setRegex(this.pathResolver.compilePathSpec(pathAnnotation.value()));
		
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
		
		//Authenticated annotation
		Authenticated authAnnotation = method.getAnnotation(Authenticated.class);
		if(authAnnotation != null) {
			FilterEntry entry = new FilterEntry(
							this.pathResolver.compilePathSpec(StringUtil.filterURL(this.getContextPath() + holder.getPathSpec())).pattern(), 
							holder.getHttpMethod()
							);
			
			entry.addProduces(holder.getProduces());
			
			if(holder.getAccepts().isPresent()) {
				entry.addAccepts(holder.getAccepts().get());
			}
			
			this.authFilter.addPattern(entry);
		}
		
		NightWeb.getEventManager().fireEvent(new RouteAddedEvent(holder));
		
		LOGGER.info("Registering route \00BB" + holder.getPathSpec() + "\00AB using Method " + holder.getHttpMethod() + "");
		
		this.routes.add(holder);
	}
	
	@Override
	public List<MethodHolder> getRoutes() {
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
		MethodHolder holder = this.routes.stream().filter(h -> h.getMethod().equals(method)).findAny().orElse(null);
		
		if(holder == null) {
			return null;
		}
		
		return StringUtil.filterURL(this.ctx.getContextPath() + holder.getPathSpec());
	}
	
	@Override
	public String getPath(String controllerAndMethodName) {
		List<String> contents = Arrays.stream(controllerAndMethodName.split("\\.")).collect(Collectors.toList());
		
		String methodName = contents.get(contents.size() - 1);
		contents.remove(contents.size() - 1);
		
		String controllerName = String.join(".", contents);
		
		for(MethodHolder holder : this.routes) {
			if(holder.getController().getClass().getCanonicalName().equals(controllerName)) {
				if(holder.getMethod().getName().equals(methodName)) {
					return StringUtil.filterURL(this.ctx.getContextPath() + holder.getPathSpec());
				}
			}
		}
		
		return this.getContextPath();
	}
	
	@Override
	public Controller getControllerByURL(String url) {
		MethodHolder holder = this.routes.stream().filter(h -> h.getRegex().matcher(url).matches()).findAny().orElse(null);
		
		Controller c = null;
		
		if(holder != null) {
			c = holder.getController();
		}
		
		if(c == null) {
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
		List<MethodHolder> holders = this.routes.stream().filter(h -> h.getRegex().matcher(query.url()).matches()).collect(Collectors.toList());
		
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
	public MethodHolder getMethodByURL(RouteQuery query) {
		return this.routes.stream()
				.filter(h -> h.getRegex().matcher(query.url()).matches())
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
	public MethodHolder getMethod(RouteQuery query) {
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
				.filter(h -> h.getRegex().matcher(path).matches())
				.findAny()
				.orElse(null) 
				!= null;
	}

}
