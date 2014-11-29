package net.shrimpworks.zomb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public static class UserRegistryImpl implements UserRegistry {

		private final Set<User> users;

		public UserRegistryImpl() {
			this.users = new HashSet<>();
		}

		@Override
		public boolean add(User entity) {
			return users.add(entity);
		}

		@Override
		public User remove(User entity) {
			return users.remove(entity) ? entity : null;
		}

		@Override
		public User find(String name) {
			for (User user : users) {
				if (user.name().equals(name)) {
					return user;
				}
			}
			return null;
		}

		@Override
		public List<User> all() {
			return Collections.unmodifiableList(new ArrayList<>(users));
		}
	}

	public static class UserImpl implements User {

		private final String name;

		public UserImpl(String name) {
			this.name = name;
		}

		@Override
		public String name() {
			return name;
		}
	}
}
