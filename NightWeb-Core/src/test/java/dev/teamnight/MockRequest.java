/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

/**
 * @author Jonas
 *
 */
public class MockRequest implements HttpServletRequest {

	private String requestURI = "/";
	private String httpMethod = "GET";
	private Map<String, String> headers = new HashMap<String, String>();
	private String requestBody;
	
	/**
	 * @param requestURI
	 * @param httpMethod
	 * @param requestBody
	 */
	public MockRequest(String requestURI, String httpMethod, String requestBody) {
		this.requestURI = requestURI;
		this.httpMethod = httpMethod;
		this.requestBody = requestBody;
	}
	
	public void addHeader(String name, String value) {
		this.headers.put(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		return "UTF-8";
	}

	@Override
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
	}

	@Override
	public int getContentLength() {
		return this.requestBody.length();
	}

	@Override
	public long getContentLengthLong() {
		return this.requestBody.length();
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public String getParameter(String name) {
		return null;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return null;
	}

	@Override
	public String[] getParameterValues(String name) {
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return null;
	}

	@Override
	public String getProtocol() {
		return "HTTP/1.1";
	}

	@Override
	public String getScheme() {
		return "http";
	}

	@Override
	public String getServerName() {
		return "localhost";
	}

	@Override
	public int getServerPort() {
		return 8080;
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return null;
	}

	@Override
	public String getRemoteAddr() {
		return "127.0.0.1";
	}

	@Override
	public String getRemoteHost() {
		return "localhost";
	}

	@Override
	public void setAttribute(String name, Object o) {
	}

	@Override
	public void removeAttribute(String name) {
	}

	@Override
	public Locale getLocale() {
		return Locale.GERMAN;
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return null;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	@Override
	public String getRealPath(String path) {
		return null;
	}

	@Override
	public int getRemotePort() {
		return 123;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public String getLocalAddr() {
		return "127.0.0.1";
	}

	@Override
	public int getLocalPort() {
		// TODO Auto-generated method stub
		return 123;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return null;
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {
		return null;
	}

	@Override
	public boolean isAsyncStarted() {
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		return false;
	}

	@Override
	public AsyncContext getAsyncContext() {
		return null;
	}

	@Override
	public DispatcherType getDispatcherType() {
		return DispatcherType.REQUEST;
	}

	@Override
	public String getAuthType() {
		return "Basic";
	}

	@Override
	public Cookie[] getCookies() {
		return new Cookie[] {};
	}

	@Override
	public long getDateHeader(String name) {
		return 0;
	}

	@Override
	public String getHeader(String name) {
		return this.headers.get(name);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		return Collections.enumeration(this.headers.values());
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return Collections.enumeration(this.headers.keySet());
	}

	@Override
	public int getIntHeader(String name) {
		return 0;
	}

	@Override
	public String getMethod() {
		return this.httpMethod;
	}

	@Override
	public String getPathInfo() {
		return this.requestURI;
	}

	@Override
	public String getPathTranslated() {
		return this.requestURI;
	}

	@Override
	public String getContextPath() {
		return "/";
	}

	@Override
	public String getQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		return this.requestURI;
	}

	@Override
	public StringBuffer getRequestURL() {
		return new StringBuffer(this.requestURI);
	}

	@Override
	public String getServletPath() {
		return null;
	}

	@Override
	public HttpSession getSession(boolean create) {
		return null;
	}

	@Override
	public HttpSession getSession() {
		return null;
	}

	@Override
	public String changeSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void login(String username, String password) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logout() throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

}
