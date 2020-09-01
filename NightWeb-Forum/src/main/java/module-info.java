/**
 * Copyright (c) 2020 Jonas Müller, Jannik Müller
 */
/**
 * @author Jonas
 *
 */
module dev.teamnight.nightweb.forum {
	exports dev.teamnight.nightweb.forum;
	exports dev.teamnight.nightweb.forum.entities;
	
	opens dev.teamnight.nightweb.forum to dev.teamnight.nightweb.core, log4j.api;
	opens dev.teamnight.nightweb.forum.entities;

	requires transitive dev.teamnight.nightweb.core;
}