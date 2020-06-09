/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.mvc.annotations.Accepts;
import dev.teamnight.nightweb.core.mvc.annotations.GET;
import dev.teamnight.nightweb.core.mvc.annotations.POST;
import dev.teamnight.nightweb.core.mvc.annotations.Path;
import dev.teamnight.nightweb.core.mvc.annotations.PathParam;
import dev.teamnight.nightweb.core.mvc.annotations.Produces;
import dev.teamnight.nightweb.core.mvc.annotations.QueryParam;

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

	public ServletRouterImpl(Context ctx) {
		this.ctx = ctx;
		this.pathResolver = new PathResolver();
	}
	
	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		if(!(req instanceof HttpServletRequest && res instanceof HttpServletResponse)) {
			throw new ServletException("non-HTTP request or response");
		}
		
		this.service((HttpServletRequest) req, (HttpServletResponse) res);
	}
	
	/**
	 * Main method for the Router to be called when a route is accessed
	 */
	protected void service(HttpServletRequest req, HttpServletResponse res) {
		
	}
	
	@Override
	public void addController(Controller controller) {
		
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
		
		holder.setPathSpec(pathAnnotation.value());
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
	public String getPath(Controller controller) {
		return "";
	}
	
	@Override
	public String getPath(Controller controller, Method method) {
		return "";
	}
	
	@Override
	public String getPath(String controllerAndMethodName) {
		return "";
	}
	
	@Override
	public Controller getControllerByURL(String url) {
		return null;
	}
	
	@Override
	public MethodHolder getMethodByURL(String url) {
		return null;
	}
	
	@Override
	public Controller getController(String pathSpec) {
		return null;
	}
	
	@Override
	public MethodHolder getMethod(String pathSpec) {
		return null;
	}

}
