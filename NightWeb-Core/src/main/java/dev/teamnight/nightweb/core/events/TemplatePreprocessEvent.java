/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.events;

import java.util.Optional;

import dev.teamnight.nightweb.core.Context;
import dev.teamnight.nightweb.core.template.TemplateBuilder;

/**
 * An event that gets fired when the Template Manager gets a request to create a Template Builder.
 * The event contains a reference to that builder and an Optional containing the context depending
 * which builder() method gets called.
 * @author Jonas
 */
public class TemplatePreprocessEvent implements Event {

	private TemplateBuilder templateBuilder;
	private Optional<Context> context;
	
	/**
	 * @param templateBuilder
	 * @param context
	 */
	public TemplatePreprocessEvent(TemplateBuilder templateBuilder, Context ctx) {
		this.templateBuilder = templateBuilder;
		this.context = Optional.ofNullable(ctx);
	}

	@Override
	public Optional<Context> getContext() {
		return this.context;
	}
	
	/**
	 * @return the templateBuilder
	 */
	public TemplateBuilder getTemplateBuilder() {
		return templateBuilder;
	}
	
}
