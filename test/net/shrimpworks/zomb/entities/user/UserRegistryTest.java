package net.shrimpworks.zomb.entities.user;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserRegistryTest {

	@Test
	public void userRegistryTest() {
		UserRegistry users = new UserRegistryImpl();

		User bob = new UserImpl("bob");
		User joe = new UserImpl("joe");

		assertTrue(users.all().isEmpty());
		assertNull(users.find("pete"));

		assertTrue(users.add(bob));
		assertEquals(bob, users.find("bob"));

		assertTrue(users.add(joe));
		assertEquals(joe, users.find("joe"));
		assertNotEquals(users.find("bob"), users.find("joe"));

		assertEquals(bob, users.remove(bob));

		assertNull(users.find("bob"));
		assertEquals(joe, users.find("joe"));

		assertTrue(users.all().contains(joe));
	}

}
