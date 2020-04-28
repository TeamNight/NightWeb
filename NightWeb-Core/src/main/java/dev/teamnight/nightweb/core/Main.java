/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import dev.teamnight.nightweb.core.impl.JettyServer;
import dev.teamnight.nightweb.core.impl.NightWebCoreImpl;

public class Main {

	public static void main(String[] args) {
		NightWebCore nightweb = new NightWebCoreImpl();
		NightWeb.setCoreApplication(nightweb);
		
		System.out.println(nightweb.getImplementationName());
		System.out.println("Jetty is active: " + ((JettyServer)nightweb.getServer()).getJettyServer().getState());
	}
	
}
