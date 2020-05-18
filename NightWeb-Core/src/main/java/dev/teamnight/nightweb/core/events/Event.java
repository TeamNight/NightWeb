/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.events;

import java.util.Optional;

import dev.teamnight.nightweb.core.Context;

/**
 * @author Jonas
 *
 */
public interface Event {
	
	public Optional<Context> getContext();

}
