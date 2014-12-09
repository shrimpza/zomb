package net.shrimpworks.zomb.entities.application;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ApplicationRegistryTest {

	@Test
	public void appRegistryTest() throws IOException {
		ApplicationRegistry reg = new ApplicationRegistryImpl(new NopApplicationPersistence());

		Application app1 = new ApplicationImpl("app1", "abc", "url", "guy");
		Application app2 = new ApplicationImpl("app2", "123", "earl", "person@mail");

		assertTrue(reg.add(app1));
		assertEquals(app1, reg.find("app1"));
		assertEquals(app1, reg.forKey("abc"));
		assertEquals(app1, reg.remove(app1));
		assertNull(reg.remove(app1));
		assertNull(reg.find("app1"));
		assertNull(reg.forKey("abc"));

		assertTrue(reg.all().isEmpty());

		assertTrue(reg.add(app1));
		assertTrue(reg.add(app2));

		assertTrue(reg.all().contains(app1));
		assertTrue(reg.all().contains(app2));

		assertEquals(app2, reg.find("app2"));
		assertEquals(app2, reg.forKey("123"));
	}


}
