/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.events;

import java.util.List;

/**
 * @author Jonas
 *
 */
public interface EventManager {

	public void registerListener(EventListener listener);
	
	public void registerListener(Class<? extends Event> event, EventListener listener);
	
	public List<EventListener> getListeners();
	
	public List<EventListener> getListeners(Class<? extends Event> event);
	
	public void clearListeners();
	
	public void clearListeners(Class<? extends Event> event);
	
	public void fireEvent(Event event);
	
}
