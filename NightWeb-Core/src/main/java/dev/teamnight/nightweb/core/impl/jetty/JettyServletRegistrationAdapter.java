/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.impl.jetty;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.Source;

import dev.teamnight.nightweb.core.PathRegistry;
import dev.teamnight.nightweb.core.ServletRegistrationAdapter;
import dev.teamnight.nightweb.core.util.ServletBuilder;

/**
 * @author Jonas
 *
 */
public class JettyServletRegistrationAdapter implements ServletRegistrationAdapter {

	private static final Logger LOGGER = LogManager.getLogger(JettyServletRegistrationAdapter.class);
	
	private ServletContextHandler handler;
	private PathRegistry pathRegistry;
	
	public JettyServletRegistrationAdapter(ServletContextHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public String getContextPath() {
		return this.handler.getContextPath();
	}
	
	@Override
	public List<HttpServlet> getServlets() {
		return Arrays.stream(handler.getServletHandler().getServlets()).map(h -> {
			try {
				return h.getServlet();
			} catch (ServletException e) {
				e.printStackTrace();
			}
			return null;
		}).filter(s -> s != null).map(s -> (HttpServlet)s).collect(Collectors.toList());
	}

	@Override
	public List<Filter> getFilters() {
		return Arrays.stream(handler.getServletHandler().getFilters()).map(h -> h.getFilter()).filter(f -> f != null).collect(Collectors.toList());
	}

	@Override
	public void addServlet(Class<? extends HttpServlet> servlet, String path) {
		ServletHolder holder = new ServletHolder(servlet);
		
		this.handler.addServlet(holder, path);
		try {
			this.pathRegistry.addServlet(path, holder.getServlet());
		} catch (ServletException e) {
			LOGGER.error("Unable to initialize servlet for registration: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void addServlet(String className, String path) {
		ServletHolder holder = new ServletHolder(Source.EMBEDDED);
		holder.setClassName(className);
		
		this.handler.addServlet(holder, path);
		try {
			this.pathRegistry.addServlet(path, holder.getServlet());
		} catch (ServletException e) {
			LOGGER.error("Unable to initialize servlet for registration: " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	@Override
	public void addServlet(ServletBuilder builder, String path) {
		ServletHolder holder = null;
		
		if(builder.getClassName().isPresent()) {
			holder = new ServletHolder(Source.EMBEDDED);
			holder.setClassName(builder.getClassName().get());
		} else if(builder.getHeldClass().isPresent()) {
			holder = new ServletHolder(builder.getHeldClass().get());
		} else {
			holder = new ServletHolder(builder.getServlet().get());
		}
		
		if(builder.getServletName().isPresent()) {
			holder.setName(builder.getServletName().get());
		}
		
		if(builder.getInitParameters().size() > 0) {
			holder.setInitParameters(builder.getInitParameters());
		}
		
		this.handler.addServlet(holder, path);
		try {
			this.pathRegistry.addServlet(path, holder.getServlet());
		} catch (ServletException e) {
			LOGGER.error("Unable to initialize servlet for registration: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void addFilter(Class<? extends Filter> filter, String path, EnumSet<DispatcherType> dispatches) {
		this.handler.addFilter(filter, path, dispatches);
	}

	@Override
	public void addFilter(String className, String path, EnumSet<DispatcherType> dispatches) {
		this.handler.addFilter(className, path, dispatches);
	}
	
	@Override
	public void addFilter(Filter filter, String path, EnumSet<DispatcherType> dispatches) {
		FilterHolder holder = new FilterHolder(filter);
		this.handler.addFilter(holder, path, dispatches);
	}

	@Override
	public void setHostnames(String[] hostnames) {
		this.handler.setVirtualHosts(hostnames);
	}

	@Override
	public void setPathRegistry(PathRegistry pathRegistry) {
		this.pathRegistry = pathRegistry;
	}
	
	public void updatePathRegistry() {
		Map<String, String> mappings = Arrays.stream(this.handler.getServletHandler().getFilterMappings()).collect(Collectors.toMap(FilterMapping::getFilterName, fm -> fm.getPathSpecs()[0]));
				
		Map<String, Filter> filtersMap = Arrays.stream(this.handler.getServletHandler().getFilters()).collect(Collectors.toMap(FilterHolder::getName, FilterHolder::getFilter));
		
		for(Entry<String, Filter> entry : filtersMap.entrySet()) {
			this.pathRegistry.addFilter(mappings.get(entry.getKey()), entry.getValue());
		}
	}

}
