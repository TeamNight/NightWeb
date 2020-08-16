/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import dev.teamnight.nightweb.core.impl.NightWebCoreImpl;
import dev.teamnight.nightweb.core.impl.jetty.JettyServer;

public class Main {

	public static void main(String[] args) {
		NightWebCore nightweb = new NightWebCoreImpl();
		
		
		System.out.println("NightWebCore Implementation: " + nightweb.getImplementationName());
		System.out.println("Jetty State: " + ((JettyServer)nightweb.getServer()).getJettyServer().getState());
	}
	
}
