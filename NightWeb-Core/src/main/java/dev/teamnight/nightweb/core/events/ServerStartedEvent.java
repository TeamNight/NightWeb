/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.events;

import java.util.Optional;

import dev.teamnight.nightweb.core.Context;

/**
 * This event gets called by the server implementation once
 * all has been setup up and the server started accepting
 * requests.
 * @author Jonas
 */
public class ServerStartedEvent implements Event {

	@Override
	public Optional<Context> getContext() {
		return Optional.empty();
	}

}
