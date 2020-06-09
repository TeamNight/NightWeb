/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.mvc;

import org.eclipse.jetty.http.HttpStatus;

import dev.teamnight.nightweb.core.Context;

/**
 * @author Jonas
 *
 */
public abstract class Controller {

	private Context context;
	
	public Controller(Context ctx) {
		this.context = ctx;
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public Result ok() {
		return new Result().status(HttpStatus.OK_200);
	}
	
	public Result ok(String content) {
		return new Result().status(HttpStatus.OK_200).content(content);
	}
	
	public Result ok(Object data) {
		return new Result().status(HttpStatus.OK_200).data(data);
	}
	
	public Result created() {
		return new Result().status(HttpStatus.CREATED_201);
	}
	
	public Result created(String content) {
		return new Result().status(HttpStatus.CREATED_201).content(content);
	}
	
	public Result created(Object data) {
		return new Result().status(HttpStatus.CREATED_201).data(data);
	}
	
	public Result accepted() {
		return new Result().status(HttpStatus.ACCEPTED_202);
	}
	
	public Result accepted(String content) {
		return new Result().status(HttpStatus.ACCEPTED_202).content(content);
	}
	
	public Result accepted(Object data) {
		return new Result().status(HttpStatus.ACCEPTED_202).data(data);
	}
	
	public Result badRequest() {
		return new Result().status(HttpStatus.BAD_REQUEST_400);
	}
	
	public Result badRequest(String errorMessage) {
		return this.error(HttpStatus.BAD_REQUEST_400, errorMessage);
	}
	
	public Result unauthorized() {
		return this.error(HttpStatus.UNAUTHORIZED_401);
	}
	
	public Result unauthorized(String errorMessage) {
		return this.error(HttpStatus.UNAUTHORIZED_401, errorMessage);
	}
	
	public Result forbidden() {
		return this.error(HttpStatus.FORBIDDEN_403);
	}
	
	public Result forbidden(String errorMessage) {
		return this.error(HttpStatus.FORBIDDEN_403, errorMessage);
	}
	
	public Result notFound() {
		return this.error(HttpStatus.NOT_FOUND_404);
	}
	
	public Result notFound(String errorMessage) {
		return this.error(HttpStatus.NOT_FOUND_404, errorMessage);
	}
	
	public Result serverError() {
		return this.error(HttpStatus.INTERNAL_SERVER_ERROR_500);
	}
	
	public Result serverError(String errorMessage) {
		return this.error(HttpStatus.INTERNAL_SERVER_ERROR_500, errorMessage);
	}
	
	public Result notImplemented() {
		return this.error(HttpStatus.NOT_IMPLEMENTED_501);
	}
	
	public Result notImplemented(String errorMessage) {
		return this.error(HttpStatus.NOT_IMPLEMENTED_501, errorMessage);
	}
	
	public Result serviceUnavailable() {
		return this.error(HttpStatus.SERVICE_UNAVAILABLE_503);
	}
	
	public Result serviceUnavailable(String errorMessage) {
		return this.error(HttpStatus.SERVICE_UNAVAILABLE_503, errorMessage);
	}
	
	public Result error(int sc) {
		return new Result().status(sc);
	}
	
	public Result error(int sc, String em) {
		return new Result().status(sc, em);
	}
}
