/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.teamnight.nightweb.core.Server;

/**
 * The PathResolver will create a pattern for a path spec
 *  and will extract the path parameters from a httpRequest URL
 *  for an aforementioned path spec.
 * @author Jonas
 *
 */
public class PathResolver {

	public static final String PRE_PATTERN = "^";
	public static final String VAR_PATTERN = "(?<$0>[^/]+)";
	public static final String POST_PATTERN = "/?$";
	
	public final String prefix;
	public final String suffix;
	public final Pattern paramPattern;
	
	public PathResolver() {
		this.prefix = ":";
		this.suffix = "";
		this.paramPattern = Pattern.compile(":[a-zA-Z0-9_.-]+");
	}
	
	public PathResolver(String paramPrefix, String paramSuffix) {
		this.prefix = paramPrefix;
		this.suffix = paramSuffix;
		this.paramPattern = Pattern.compile(paramPrefix + "[a-zA-Z0-9_.-]+" + paramSuffix);
	}
	
	public PathResolver(Server server, String paramPrefix, String paramSuffix) {
		this.prefix = paramPrefix;
		this.suffix = paramSuffix;
		this.paramPattern = Pattern.compile(paramPrefix + "[a-zA-Z0-9_.-]+" + paramSuffix);
	}
	
	public Pattern compilePathSpec(String pathSpec) {
		if(pathSpec.endsWith("/")) {
			pathSpec = pathSpec.substring(0, pathSpec.length() - 1);
		}
		
		Matcher m = this.paramPattern.matcher(pathSpec);
		
		StringBuilder sb = new StringBuilder(pathSpec);
		
		while(m.find()) {
			String parameter = m.group();
			String paramName = parameter.substring(this.prefix.length(), parameter.length() - this.suffix.length());
			
			sb.replace(m.start(), m.end(), PathResolver.VAR_PATTERN.replaceFirst("(?<=\\<)\\$0(?=\\>)", paramName));
			sb.insert(0, PathResolver.PRE_PATTERN);
			sb.append(PathResolver.POST_PATTERN);
		}
		
		return Pattern.compile(sb.toString());
	}
	
	public Map<RequestParameter, String> resolvePathParameters(Pattern pathSpec, RequestParameter[] parameters, String requestPath) {
		Map<RequestParameter, String> params = new HashMap<RequestParameter, String>();
		
		Matcher matcher = pathSpec.matcher(requestPath);
		if(!matcher.matches()) {
			throw new IllegalArgumentException("pathSpec does not match requestPath");
		}
		
		for(RequestParameter parameter : parameters) {
			if(!parameter.isURLParameter()) {
				throw new IllegalArgumentException("The parameter " + parameter.getName() + " needs isURLParamter to be true");
			}

			String value = null;
			try {
				value = matcher.group(parameter.getName());
			} catch(IllegalArgumentException e) {
				throw new IllegalArgumentException("No param was defined in the pathSpec for " + parameter.getName());
			}
			
			params.put(parameter, value);
		}
		
		return params;
	}
	
	/**
	 * @return the varPattern
	 */
	public static String getVariablePattern() {
		return VAR_PATTERN;
	}
	
}
