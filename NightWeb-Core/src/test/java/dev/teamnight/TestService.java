package dev.teamnight;

import static org.junit.Assert.assertTrue;

import org.hibernate.SessionFactory;
import org.junit.Test;

import dev.teamnight.nightweb.core.service.AbstractService;

public class TestService extends AbstractService<String> {

	public TestService() {
		super(null);
	}
	
	@Test
	public void testService() {
		assertTrue("java.lang.String".equals(this.getType().getCanonicalName()));
	}

}
