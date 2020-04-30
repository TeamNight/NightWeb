/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
package dev.teamnight.nightweb.core;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import dev.teamnight.nightweb.core.impl.JettyServer;
import dev.teamnight.nightweb.core.impl.NightWebCoreImpl;
import dev.teamnight.nightweb.core.module.ModuleMetaFile;

public class Main {

	public static void main(String[] args) {
		NightWebCore nightweb = new NightWebCoreImpl();
		NightWeb.setCoreApplication(nightweb);
		
		System.out.println("NightWebCore Implementation: " + nightweb.getImplementationName());
		System.out.println("Jetty State: " + ((JettyServer)nightweb.getServer()).getJettyServer().getState());
	}
	
}
