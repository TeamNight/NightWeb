/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * @author Jonas
 *
 */
public abstract class WebSession implements HttpSessionBindingListener {

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		NightWeb.getCoreApplication().addSession(this);
	}
	
	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		NightWeb.getCoreApplication().removeSession(this);
	}
	
}
