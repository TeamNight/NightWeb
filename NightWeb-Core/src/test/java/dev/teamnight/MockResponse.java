/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jonas
 *
 */
public class MockResponse implements HttpServletResponse {

	private String charEncoding;
	private String contentType;
	private PrintWriter writer;
	private int status;
	private String errorMsg;
	
	public MockResponse() {
		this.writer = new PrintWriter(System.out);
	}
	
	@Override
	public String getCharacterEncoding() {
		return this.charEncoding;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return this.writer;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		this.charEncoding = charset;
	}

	@Override
	public void setContentLength(int len) {
	}

	@Override
	public void setContentLengthLong(long len) {
	}

	@Override
	public void setContentType(String type) {
		this.contentType = type;
	}

	@Override
	public void setBufferSize(int size) {
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {
	}

	@Override
	public void resetBuffer() {
	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public void reset() {
	}

	@Override
	public void setLocale(Locale loc) {
	}

	@Override
	public Locale getLocale() {
		return Locale.GERMAN;
	}

	@Override
	public void addCookie(Cookie cookie) {
	}

	@Override
	public boolean containsHeader(String name) {
		return false;
	}

	@Override
	public String encodeURL(String url) {
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return null;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		this.status = sc;
		this.errorMsg = msg;
	}

	@Override
	public void sendError(int sc) throws IOException {
		this.status = sc;
	}

	@Override
	public void sendRedirect(String location) throws IOException {
	}

	@Override
	public void setDateHeader(String name, long date) {
	}

	@Override
	public void addDateHeader(String name, long date) {
	}

	@Override
	public void setHeader(String name, String value) {
	}

	@Override
	public void addHeader(String name, String value) {
	}

	@Override
	public void setIntHeader(String name, int value) {
	}

	@Override
	public void addIntHeader(String name, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(int sc) {
		this.status = sc;
	}

	@Override
	public void setStatus(int sc, String sm) {
		this.status = sc;
		this.errorMsg = sm;
	}

	@Override
	public int getStatus() {
		return this.status;
	}

	@Override
	public String getHeader(String name) {
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return null;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
}
