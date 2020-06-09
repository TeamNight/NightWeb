/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;

/**
 * @author Jonas
 *
 */
public class Result {

	private int status;
	private String statusMessage;
	
	private String contentType;
	private String characterEncoding;
	private Object data;
	
	private Map<String, String> headers;
	private List<Cookie> cookies;
	
	private String redirectLocation;
	
	public Result() {
		this.status = 200;
		this.contentType = "text/html";
		this.characterEncoding = "UTF-8";
		this.data = "";
	}
	
	private void newHeadersMap() {
		this.headers = new HashMap<String, String>();
	}
	
	private void newCookiesList() {
		this.cookies = new ArrayList<Cookie>();
	}
	
	public int status() {
		return this.status;
	}
	
	public String statusMessage() {
		return this.statusMessage;
	}
	
	public Result status(int status) {
		this.status = status;
		
		return this;
	}
	
	public Result status(int status, String statusMessage) {
		this.statusMessage = statusMessage;
		
		return this.status(status);
	}
	
	public String contentType() {
		return this.contentType;
	}
	
	public Result contentType(String contentType) {
		this.contentType = contentType;
		
		return this;
	}
	
	public String characterEncoding() {
		return this.characterEncoding;
	}
	
	public Result characterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
		
		return this;
	}
	
	public String content() {
		if(this.data instanceof String) {
			return (String) this.data;
		} else {
			return "";
		}
	}
	
	public Result content(String content) {
		this.data = content;
		
		return this;
	}
	
	public Object data() {
		return this.data;
	}
	
	public Result data(Object data) {
		this.data = data;
		
		return this;
	}
	
	public Map<String, String> headers() {
		if(this.headers == null) {
			this.newHeadersMap();
		}
		
		return this.headers;
	}
	
	public Result withHeaders(Map<String, String> header) {
		if(this.headers == null) {
			this.newHeadersMap();
		}
		
		this.headers.putAll(header);
		
		return this;
	}
	
	public Result withHeader(String name, String value) {
		if(this.headers == null) {
			this.newHeadersMap();
		}
		
		this.headers.put(name, value);
		
		return this;
	}
	
	public List<Cookie> cookies() {
		if(this.cookies == null) {
			this.newCookiesList();
		}
		
		return this.cookies;
	}
	
	public Optional<Cookie> cookie(String name) {
		if(this.cookies == null) {
			return Optional.empty();
		}
		
		for(Cookie cookie : this.cookies) {
			if(cookie.getName().equals(name)) {
				return Optional.of(cookie);
			}
		}
		
		return Optional.empty();
	}
	
	public Result withCookies(Collection<Cookie> cookies) {
		if(this.cookies == null) {
			this.newCookiesList();
		}
		
		this.cookies.addAll(cookies);

		return this;
	}
	
	public Result withCookies(Cookie...cookies) {
		if(this.cookies == null) {
			this.newCookiesList();
		}
		
		for(Cookie cookie : cookies) {
			this.cookies.add(cookie);
		}

		return this;
	}
	
	public Result withCookie(Cookie cookie) {
		if(this.cookies == null) {
			this.newCookiesList();
		}
		
		this.cookies.add(cookie);
		
		return this;
	}
	
	public Optional<String> redirect() {
		return Optional.ofNullable(this.redirectLocation);
	}
	
	public Result redirect(String location) {
		this.redirectLocation = location;
		
		return this;
	}

}
