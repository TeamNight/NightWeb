package dev.teamnight.nightweb.core.template;

import java.io.PrintStream;
import java.io.PrintWriter;

public class TemplateProcessException extends RuntimeException {

	private static final long serialVersionUID = 1328441557690538715L;
	private Exception exception;
	
	public TemplateProcessException(Exception e) {
		this.exception = e;
	}
	
	public Exception getException() {
		return exception;
	}
	
	@Override
	public void printStackTrace() {
		this.exception.printStackTrace();
	}
	
	@Override
	public void printStackTrace(PrintStream s) {
		this.exception.printStackTrace(s);
	}
	
	@Override
	public void printStackTrace(PrintWriter s) {
		this.exception.printStackTrace(s);
	}

}
