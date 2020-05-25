/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

/**
 * @author Jonas
 *
 */
public class DefaultEventManagerImpl implements EventManager {

	private List<ListenerHolder> listeners = new CopyOnWriteArrayList<ListenerHolder>();
	
	@Override
	public void registerListener(EventListener listener) {
		this.registerListener(null, listener);
	}

	@Override
	public void registerListener(Class<? extends Event> event, EventListener listener) {
		if(!Event.class.isAssignableFrom(event)) {
			throw new IllegalArgumentException("Tried to register class that does not implement event");
		}
		
		ListenerHolder holder = new ListenerHolder(listener, event);
		this.listeners.add(holder);
	}

	@Override
	public List<EventListener> getListeners() {
		return this.listeners.stream().map(holder -> holder.getListener()).collect(Collectors.toUnmodifiableList());
	}

	@Override
	public List<EventListener> getListeners(Class<? extends Event> event) {
		return this.listeners.stream()
				.filter(holder -> holder.getSpecificEvent() != null)
				.filter(holder -> event.isAssignableFrom(holder.getSpecificEvent()))
				.map(holder -> holder.getListener())
				.collect(Collectors.toUnmodifiableList());
	}

	@Override
	public void clearListeners() {
		this.listeners.clear();
	}

	@Override
	public void clearListeners(Class<? extends Event> event) {
		this.listeners.removeIf(holder -> holder.getSpecificEvent() != null && event.isAssignableFrom(holder.getSpecificEvent()));
	}

	@Override
	public void fireEvent(Event event) {
		LogManager.getLogger().debug("Executing event: " + event.getClass().getSimpleName());
		for(ListenerHolder holder : this.listeners) {
			if(holder.getSpecificEvent() != null) {
				if(!holder.getSpecificEvent().isInstance(event)) {
					break;
				}
			}
			
			holder.getListener().onEvent(event);
		}
	}

	/**
	 * Class for saving listeners adding the possibility for listeners to listen on specific events
	 * @author Jonas
	 *
	 */
	private class ListenerHolder {
		
		private EventListener listener;
		private Class<? extends Event> specificEvent;
		
		/**
		 * @param listener
		 * @param specificEvent
		 */
		public ListenerHolder(EventListener listener, Class<? extends Event> specificEvent) {
			this.listener = listener;
			this.specificEvent = specificEvent;
		}
		/**
		 * @return the listener
		 */
		public EventListener getListener() {
			return listener;
		}
		/**
		 * @return the specificEvent
		 */
		public Class<? extends Event> getSpecificEvent() {
			return specificEvent;
		}
		/**
		 * @param listener the listener to set
		 */
		@SuppressWarnings("unused")
		public void setListener(EventListener listener) {
			this.listener = listener;
		}
		/**
		 * @param specificEvent the specificEvent to set
		 */
		@SuppressWarnings("unused")
		public void setSpecificEvent(Class<? extends Event> specificEvent) {
			this.specificEvent = specificEvent;
		}
		
	}
	
}
