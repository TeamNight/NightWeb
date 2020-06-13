/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.events;

/**
 * An interface for classes that want to listen for a specific event fired by any application
 * @author Jonas
 */
@FunctionalInterface
public interface EventListener {
	/**
	 * The onEvent method gets called by an implementation of EventManager when any class fires an Event using {@link dev.teamnight.nightweb.core.events.EventManager#fireEvent(Event)}.
	 * Depending how the listener was registered this might be any event or a specific event.
	 * @param T the event that was fired
	 */
	public void onEvent(Event e);
}
